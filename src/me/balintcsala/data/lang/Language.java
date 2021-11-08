package me.balintcsala.data.lang;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {

    private static final Pattern SUBSTITUTION_EXTRACTOR = Pattern.compile("^([^\\.]+)\\.([^\\s=]+)=(.+)$");

    private HashMap<String, String> screenSubstitutions = new HashMap<>();
    private HashMap<String, String> optionSubstitutions = new HashMap<>();
    private HashMap<String, HashMap<String, String>> valueSubstitutions = new HashMap<>();

    private Language() { }

    public static Language parseLanguageFile(File file) {
        Language language = new Language();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Matcher matcher;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty())
                    continue;

                matcher = SUBSTITUTION_EXTRACTOR.matcher(line);
                if (!matcher.find()) {
                    System.err.println("Failed to parse language file line: \"" + line + "\"");
                    continue;
                }
                switch (matcher.group(1)) {
                    case "screen":
                        language.screenSubstitutions.put(matcher.group(2), matcher.group(3));
                        break;
                    case "option":
                        language.optionSubstitutions.put(matcher.group(2), matcher.group(3));
                        break;
                    case "value":
                        String[] parts = matcher.group(2).split("\\.", 2);
                        String name = parts[0];
                        String value = parts[1];
                        if (!language.valueSubstitutions.containsKey(name))
                            language.valueSubstitutions.put(name, new HashMap<>());

                        HashMap<String, String> container = language.valueSubstitutions.get(name);
                        container.put(value, matcher.group(3));
                        break;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return language;
    }

    public String getScreenName(String id) {
        if (screenSubstitutions.containsKey(id))
            return screenSubstitutions.get(id);
        return id;
    }

    public String getValueName(String id, String value) {
        if (valueSubstitutions.containsKey(id) && valueSubstitutions.get(id).containsKey(value))
            return valueSubstitutions.get(id).get(value);
        return value;
    }

    public String getOptionName(String id) {
        if (optionSubstitutions.containsKey(id))
            return optionSubstitutions.get(id);
        return id;
    }

}
