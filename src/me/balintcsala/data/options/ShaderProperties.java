package me.balintcsala.data.options;

import me.balintcsala.ui.ProgressBarDialog;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderProperties {

    private static final Pattern OPTION_DEFAULT_VALUE_COMMENT = Pattern.compile("#define\\s+(\\S+)\\s+(\\S+)\\s+//(.+)$");
    private static final Pattern OPTION_DEFAULT_VALUE = Pattern.compile("#define\\s+(\\S+)\\s+(\\S+)\\s*$");
    private static final Pattern OPTION_BOOLEAN_TRUE_COMMENT = Pattern.compile("#define\\s+(\\S+)\\s*//(.+)$");
    private static final Pattern OPTION_BOOLEAN_FALSE_COMMENT = Pattern.compile("//(?:\\s+)?#define\\s+(\\S+)\\s*//(.+)$");
    private static final Pattern OPTION_BOOLEAN_TRUE = Pattern.compile("#define\\s+(\\S+)\\s*$");
    private static final Pattern OPTION_BOOLEAN_FALSE = Pattern.compile("//(?:\\s+)?#define\\s+(\\S+)\\s*$");
    private static final Pattern CONST_OPTION = Pattern.compile("const\\s+\\S+\\s+(\\S+)\\s*=\\s*(-?\\d*\\.?\\d*);\\s*//(.+)$");
    private static final Pattern VALUE_LIST = Pattern.compile("(?<=\\[)(.+)(?=])");
    private static final Pattern SLIDER_EXTRACTOR = Pattern.compile("sliders\\s*=\\s*(.+)$");

    private static final List<String> AVAILABLE_CONST_OPTIONS = Arrays.asList(
            "shadowMapResolution",
            "shadowDistance",
            "shadowDistanceRenderMul",
            "shadowIntervalSize",
            "generateShadowMipmap",
            "generateShadowColorMipmap",
            "shadowHardwareFiltering",
            "shadowHardwareFiltering0",
            "shadowHardwareFiltering1",
            "shadowtex0Mipmap",
            "shadowtexMipmap",
            "shadowtex1Mipmap",
            "shadowcolor0Mipmap",
            "shadowColor0Mipmap",
            "shadowcolor1Mipmap",
            "shadowColor1Mipmap",
            "shadowtex0Nearest",
            "shadowtexNearest",
            "shadow0MinMagNearest",
            "shadowtex1Nearest",
            "shadow1MinMagNearest",
            "shadowcolor0Nearest",
            "shadowColor0Nearest",
            "shadowColor0MinMagNearest",
            "shadowcolor1Nearest",
            "shadowColor1Nearest",
            "shadowColor1MinMagNearest",
            "wetnessHalflife",
            "drynessHalflife",
            "eyeBrightnessHalflife",
            "centerDepthHalflife",
            "sunPathRotation",
            "ambientOcclusionLevel",
            "superSamplingLevel",
            "noiseTextureResolution"
    );

    private final HashMap<String, Option> options = new HashMap<>();
    private final HashMap<String, Screen> screens = new HashMap<>();
    private ArrayList<String> sliders = new ArrayList<>();

    private ShaderProperties() {
    }

    private void parseOption(Option.Type type, String name, String defaultValue, String comment, File file, int line) {
        parseOption(type, name, defaultValue, comment, file, line, false);
    }

    private void parseOption(Option.Type type, String name, String defaultValue, String comment, File file, int line, boolean constOption) {
        if (options.containsKey(name)) {
            Option option = options.get(name);
            option.addLocation(file, line);
            options.replace(name, option);
        } else {
            Matcher matcher = VALUE_LIST.matcher(comment);
            String[] values = type == Option.Type.BOOLEAN ? new String[] { "ON", "OFF" } :
                    matcher.find() ? matcher.group(1).split(" ") : new String[0];
            Option option;
            if (constOption) {
                option = new ConstOption(type, name, defaultValue, values, comment);
            } else {
                option = new Option(type, name, defaultValue, values, comment);
            }
            option.addLocation(file, line);
            options.put(name, option);
        }
    }

    private void parseShader(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher matcher;
                if ((matcher = CONST_OPTION.matcher(line)).find()) {
                    if (!AVAILABLE_CONST_OPTIONS.contains(matcher.group(1)))
                        continue;

                    String defaultValue = matcher.group(2);
                    if ("true".equals(defaultValue) || "false".equals(defaultValue)) {
                        parseOption(Option.Type.BOOLEAN, matcher.group(1), "true".equals(defaultValue) ? "ON" : "OFF", matcher.group(3), file, lineNumber, true);
                    } else {
                        parseOption(Option.Type.VALUE, matcher.group(1), defaultValue, matcher.group(3), file, lineNumber, true);
                    }
                } else if ((matcher = OPTION_DEFAULT_VALUE_COMMENT.matcher(line)).find()) {
                    parseOption(Option.Type.VALUE, matcher.group(1), matcher.group(2), matcher.group(3), file, lineNumber);
                } else if ((matcher = OPTION_DEFAULT_VALUE.matcher(line)).find()) {
                    parseOption(Option.Type.VALUE, matcher.group(1), matcher.group(2), "", file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_FALSE_COMMENT.matcher(line)).find()) {
                    parseOption(Option.Type.BOOLEAN, matcher.group(1), "OFF", matcher.group(2), file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_TRUE_COMMENT.matcher(line)).find()) {
                    parseOption(Option.Type.BOOLEAN, matcher.group(1), "ON", matcher.group(2), file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_FALSE.matcher(line)).find()) {
                    parseOption(Option.Type.BOOLEAN, matcher.group(1), "OFF", "", file, lineNumber);
                } else if ((matcher = OPTION_BOOLEAN_TRUE.matcher(line)).find()) {
                    parseOption(Option.Type.BOOLEAN, matcher.group(1), "ON", "", file, lineNumber);
                }
                lineNumber++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseShadersPropertiesLine(String line) {
        if (line.trim().startsWith("screen")) {
            Screen screen = Screen.parseScreen(line);
            if (screen == null)
                return;
            screens.put(screen.getName(), screen);
        } else if (line.trim().startsWith("sliders")) {
            Matcher matcher = SLIDER_EXTRACTOR.matcher(line);
            if (!matcher.find())
                return;

            sliders = new ArrayList<>(Arrays.asList(matcher.group(1).split(" ")));
        }
    }

    public Screen getScreen(String name) {
        return screens.get(name);
    }

    public Option getOption(String name) {
        return options.get(name);
    }

    public boolean isSlider(String name) {
        return sliders.contains(name);
    }

    public static ShaderProperties parseFiles() {
        ShaderProperties properties = new ShaderProperties();

        try {
            Files.walk(Paths.get("tmp", "shaders"))
                    .filter(path -> !path.toFile().isDirectory())
                    .forEach(path -> properties.parseShader(path.toFile()));
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

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse wildcards
        HashSet<String> processedOptions = new HashSet<>();
        for (Screen screen : properties.screens.values()) {
            for (Screen.Entry entry : screen.getEntries()) {
                if (entry.type == Screen.EntryType.OPTION) {
                    processedOptions.add(entry.name);
                }
            }
        }
        HashSet<String> remainingOptions = new HashSet<>();
        for (Option option : properties.options.values()) {
            if (!processedOptions.contains(option.name)) {
                remainingOptions.add(option.name);
            }
        }
        for (Screen screen : properties.screens.values()) {
            screen.replaceWildcard(remainingOptions);
        }

        return properties;
    }

    public void save() {
        int total = options.size();
        ProgressBarDialog progressBarDialog = new ProgressBarDialog("Saving options");

        int applied = 0;
        for (Option option : options.values()) {
            option.apply();
            applied++;
            progressBarDialog.updateProgress((double) applied / total);
        }
        progressBarDialog.dispose();

        JDialog dialog = new JDialog();
        dialog.setLayout(new GridLayout(6, 1));
        dialog.setAlwaysOnTop(true);
        dialog.setTitle("Success");

        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.add(Box.createHorizontalGlue());
        row1.add(new JLabel("Settings applied!"));
        row1.add(Box.createHorizontalGlue());
        dialog.add(row1);

        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        row2.add(Box.createHorizontalGlue());
        row2.add(new JLabel("But you're still hungry."));
        row2.add(Box.createHorizontalGlue());
        dialog.add(row2);

        JPanel emptyRow = new JPanel();
        dialog.add(emptyRow);

        JPanel row4 = new JPanel();
        row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
        row4.add(Box.createHorizontalGlue());
        row4.add(new JLabel("Open the game and select the shaderpack with the modified_ prefix"));
        row4.add(Box.createHorizontalGlue());
        dialog.add(row4);

        JPanel emptyRow2 = new JPanel();
        dialog.add(emptyRow2);

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> dialog.dispose());
        buttonRow.add(okButton);
        buttonRow.add(Box.createHorizontalGlue());
        dialog.add(buttonRow);

        dialog.setSize(450, 140);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void reset() {
        for (Option option : options.values()) {
            option.reset();
        }
    }
}
