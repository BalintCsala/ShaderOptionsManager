package me.balintcsala.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderProperties {

    private static final Pattern OPTION_DEFAULT_VALUE_COMMENT = Pattern.compile("^#define\\s+(\\S+)\\s+(\\S+)\\s+\\/\\/(.+)");
    private static final Pattern OPTION_BOOLEAN_TRUE_COMMENT = Pattern.compile("^#define\\s+(\\S+)\\s+\\/\\/(.+)");
    private static final Pattern OPTION_BOOLEAN_FALSE_COMMENT = Pattern.compile("^//(?:\\s+)?#define\\s+(\\S+)\\s+\\/\\/(.+)");
    private static final Pattern OPTION_BOOLEAN_TRUE = Pattern.compile("^#define\\s+(\\S+)");
    private static final Pattern OPTION_BOOLEAN_FALSE = Pattern.compile("^//(?:\\s+)?#define\\s+(\\S+)");
    private static final Pattern VALUE_LIST = Pattern.compile("(?<=\\[)(.+)(?=\\])");

    private final HashMap<String, Option> options = new HashMap<>();
    private final HashMap<String, Screen> screens = new HashMap<>();

    private ShaderProperties() {
    }

    private void parseOption(String name, String defaultValue, String comment, File file, int line) {
        if (options.containsKey(name)) {
            Option option = options.get(name);
            option.addLocation(file, line);
            options.replace(name, option);
        } else {
            Matcher matcher = VALUE_LIST.matcher(comment);
            String[] values = matcher.find() ? matcher.group(1).split(" ") : null;
            Option option = new Option(defaultValue, values);
            options.put(name, option);
        }
    }

    private void parseShader(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                Matcher matcher;
                if ((matcher = OPTION_DEFAULT_VALUE_COMMENT.matcher(line)).find()) {
                    parseOption(matcher.group(1), matcher.group(2), matcher.group(3), file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_TRUE_COMMENT.matcher(line)).find()) {
                    parseOption(matcher.group(1), "TRUE", matcher.group(2), file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_FALSE_COMMENT.matcher(line)).find()) {
                    parseOption(matcher.group(1), "FALSE", matcher.group(2), file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_TRUE.matcher(line)).find()) {
                    parseOption(matcher.group(1), "TRUE", "", file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_FALSE.matcher(line)).find()) {
                    parseOption(matcher.group(1), "FALSE", "", file, lineNumber);
                }
                lineNumber++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseShadersPropertiesLine(String line) {
        if (line.startsWith("screen")) {
            Screen screen = Screen.parseScreen(line);
            screens.put(screen.getName(), screen);
        }
    }

    public Screen getScreen(String name) {
        return screens.get(name);
    }

    public Option getOption(String name) {
        return options.get(name);
    }

    public static ShaderProperties parseFiles() {
        ShaderProperties properties = new ShaderProperties();

        try {
            Files.walk(Paths.get("tmp"))
                    .filter(path -> path.toString().endsWith(".vsh") || path.toString().endsWith(".fsh"))
                    .forEach(path -> {
                        properties.parseShader(path.toFile());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File shadersPropertiesFile = Paths.get("tmp", "shaders", "shaders.properties").toFile();
            BufferedReader reader = new BufferedReader(new FileReader(shadersPropertiesFile));
            StringBuilder propertyLine = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                propertyLine.append(line);
                if (line.endsWith("\\"))
                    continue;

                properties.parseShadersPropertiesLine(propertyLine.toString());

                // Clear propertyLine
                propertyLine.setLength(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

}
