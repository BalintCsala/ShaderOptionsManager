package me.balintcsala;

import me.balintcsala.data.lang.Language;
import me.balintcsala.data.options.ShaderProperties;
import me.balintcsala.ui.ProgressBarDialog;
import me.balintcsala.ui.editor.Window;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();

        Path minecraftPath = Utils.getMinecraftPath();
        File fileCooserStart;
        if (minecraftPath == null) {
            fileCooserStart = new File("");
        } else {
            fileCooserStart = minecraftPath.resolve("shaderpacks").toFile();
        }

        fileChooser.setCurrentDirectory(fileCooserStart);
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
        int res = fileChooser.showOpenDialog(null);

        if (res != JFileChooser.APPROVE_OPTION)
            return;

        File shaderpackZipFile = fileChooser.getSelectedFile();

        ZipExtractor extractor = new ZipExtractor(shaderpackZipFile);
        int total = extractor.countFiles();

        Thread extractorThread = new Thread(extractor);
        extractorThread.start();
        ProgressBarDialog dialog = new ProgressBarDialog("Extracting...");

        while (!extractor.isFinished()) {
            int processed = extractor.getProcessed();
            dialog.updateProgress((double) processed / total);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dialog.dispose();

        try {
            extractorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ShaderProperties shaderProperties = ShaderProperties.parseFiles();

        HashMap<String, Language> languages = new HashMap<>();
        try {
            Files.walk(Paths.get("tmp", "shaders", "lang"))
                    .filter(path -> path.toString().endsWith(".lang"))
                    .forEach(path -> languages.put(path.toFile().getName().replace(".lang", ""), Language.parseLanguageFile(path.toFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Window window = new Window(shaderProperties, languages, shaderpackZipFile.getName().replace(".zip", ""));

        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { }

            @Override
            public void windowClosing(WindowEvent e) { }

            @Override
            public void windowClosed(WindowEvent e) {
                //Utils.deleteDirectory(new File("tmp"));
            }

            @Override
            public void windowIconified(WindowEvent e) { }

            @Override
            public void windowDeiconified(WindowEvent e) { }

            @Override
            public void windowActivated(WindowEvent e) { }

            @Override
            public void windowDeactivated(WindowEvent e) { }
        });
    }
}
