package me.balintcsala;

import me.balintcsala.data.Utils;
import me.balintcsala.ui.editor.Window;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Utils.getMinecraftPath().toFile(), "shaderpacks"));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String[] parts = f.toString().split("\\.");
                String extension = parts[parts.length - 1];
                return "zip".equals(extension);
            }

            @Override
            public String getDescription() {
                return "*.zip";
            }
        });
        while (true) {
            int res = fileChooser.showOpenDialog(null);
            if (res != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Please select a shaderpack!");
            } else {
                break;
            }
        }
        File file = fileChooser.getSelectedFile();
        System.out.println(file.toString());

        Window window = new Window();
    }
}
