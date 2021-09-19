package me.balintcsala.data;

import java.io.File;
import java.util.ArrayList;

public class Option {

    public String currentValue;
    public String[] values;
    public ArrayList<FileLocation> locations = new ArrayList<>();

    public Option(String defaultValue, String[] values) {
        this.currentValue = defaultValue;
        this.values = values;
    }

    public void addLocation(File file, int line) {
        locations.add(new FileLocation(file, line));
    }
}
