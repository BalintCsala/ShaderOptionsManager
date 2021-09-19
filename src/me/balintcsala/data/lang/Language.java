package me.balintcsala.data.lang;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {

    private static final Pattern SUBSTITUTION_EXTRACTOR = Pattern.compile("^([^\\.]+)\\.([^\\s=]+)=(.+)$");

    private HashMap<String, String> screenSubstitutions = new HashMap<>();
    private HashMap<String, String> optionSubstitutions = new HashMap<>();
    private HashMap<String, String> valueSubstitutions = new HashMap<>();

    private Language() { }

    public static Language parseLanguageFile(File file) {
        Language language = new Language();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Matcher matcher;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
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
                        language.valueSubstitutions.put(matcher.group(2), matcher.group(3));
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

    public String getValueName(String id) {
        if (valueSubstitutions.containsKey(id))
            return valueSubstitutions.get(id);
        return id;
    }

    public String getOptionName(String id) {
        if (optionSubstitutions.containsKey(id))
            return optionSubstitutions.get(id);
        return id;
    }

}
