package me.balintcsala.data;

import java.io.File;

public class FileLocation {

    public File file;
    public int line;

    public FileLocation(File file, int line) {
        this.file = file;
        this.line = line;
    }
}
