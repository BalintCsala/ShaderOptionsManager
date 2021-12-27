package me.balintcsala.data.options;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Screen {

    public enum EntryType {
        LINK,
        OPTION,
        PROFILE,
        EMPTY,
        WILDCARD,
    }

    public class Entry {

        public EntryType type;
        public String name;

        public Entry(EntryType type, String name) {
            this.type = type;
            this.name = name;
        }

        public Entry(EntryType type) {
            this(type, "");
        }
    }

    private static final Pattern LINK_ENTRY = Pattern.compile("(?<=\\[)([^\\[\\]]+)(?=\\])");
    private static final Pattern EMPTY_ENTRY = Pattern.compile("<empty>");
    private static final Pattern PROFILE_ENTRY = Pattern.compile("(?<=<)([^<>]+)(?=>)");
    private static final Pattern WILDCARD_ENTRY = Pattern.compile("\\*");
    private static final Pattern VALUE_ENTRY = Pattern.compile("(\\S+)");

    private static final Pattern EXTRACT_MAIN_SCREEN_INFO = Pattern.compile("\\s*(?<=screen)\\s*=\\s*(.+)$");
    private static final Pattern EXTRACT_SUB_SCREEN_INFO = Pattern.compile("\\s*(?<=screen\\.)([^\\s\\[]+)\\s*=\\s*(.+)$");

    private final String name;
    private final ArrayList<Entry> entries = new ArrayList<>();

    private Screen(String name) {
        this.name = name;
    }

    public void addEntry(String description) {
        Matcher matcher;
        if ((matcher = LINK_ENTRY.matcher(description)).find()) {
            entries.add(new Entry(EntryType.LINK, matcher.group(1)));
        } else if (EMPTY_ENTRY.matcher(description).find()) {
            entries.add(new Entry(EntryType.EMPTY));
        } else if ((matcher = PROFILE_ENTRY.matcher(description)).find()) {
            //entries.add(new Entry(EntryType.PROFILE, matcher.group(1)));
            // TODO: What do <profile> entries even do?
        } else if (WILDCARD_ENTRY.matcher(description).find()) {
            entries.add(new Entry(EntryType.WILDCARD));
        } else if ((matcher = VALUE_ENTRY.matcher(description)).find()) {
            entries.add(new Entry(EntryType.OPTION, matcher.group(1)));
        }
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public String getName() {
        return name;
    }

    public static Screen parseScreen(String line) {
        Matcher matcher;

        Screen screen;
        String entryData;
        if ((matcher = EXTRACT_MAIN_SCREEN_INFO.matcher(line)).find()) {
            screen = new Screen("MAIN");
            entryData = matcher.group(1);
        } else if ((matcher = EXTRACT_SUB_SCREEN_INFO.matcher(line)).find()) {
            screen = new Screen(matcher.group(1));
            entryData = matcher.group(2);
        } else {
            System.err.println("Failed to parse screen description: \"" + line + "\"");
            return null;
        }

        String[] parts = entryData.split(" ");
        for (String part : parts) {
            screen.addEntry(part);
        }

        return screen;
    }

    public void replaceWildcard(HashSet<String> remainingOptions) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            if (entry.type == EntryType.WILDCARD) {
                entries.remove(i);
                remainingOptions.forEach(option -> addEntry(option));
            }
        }
    }
}
