package me.balintcsala.data.options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Option {

    enum Type {
        BOOLEAN,
        VALUE,
    }

    public Type type;
    public String name;
    public String defaultValue;
    public int index;
    public ArrayList<String> values;
    public ArrayList<FileLocation> locations = new ArrayList<>();

    protected String comment;

    public Option(Type type, String name, String defaultValue, String[] values, String comment) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.comment = comment;

        this.values = new ArrayList<>(Arrays.asList(values));
        if (!this.values.contains(defaultValue))
            this.values.add(0, defaultValue);

        index = this.values.indexOf(defaultValue);
    }

    public void addLocation(File file, int line) {
        locations.add(new FileLocation(file, line));
    }

    public String getCurrentValue() {
        return values.get(index);
    }

    public void nextValue() {
        index = (index + 1) % values.size();
    }

    public void previousValue() {
        index = ((index - 1) % values.size() + values.size()) % values.size();
    }

    public void setValue(String value) {
        index = values.indexOf(value);
    }

    public void reset() {
        index = this.values.indexOf(defaultValue);
    }

    protected void changeLine(List<String> lines, int line) {
        if (type == Type.BOOLEAN) {
            lines.set(line, (getCurrentValue().equals("ON") ? "" : "// ") + "#define " + name + " // " + comment);
        } else {
            lines.set(line, "#define " + name + " " + getCurrentValue() + " // " + comment);
        }
    }

    public void apply() {
        for (FileLocation location : locations) {
            Path path = location.file.toPath();
            try {
                List<String> lines = Files.readAllLines(path);
                changeLine(lines, location.line);
                Files.write(path, lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
