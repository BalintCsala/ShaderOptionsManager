package me.balintcsala;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Utils {

    public static Path getMinecraftPath() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        Path minecraftDir;
        if (os.contains("windows")) {
            // Windows
            minecraftDir = Paths.get(System.getenv("APPDATA"), ".minecraft");
        } else if (os.contains("mac")) {
            // Mac
            minecraftDir = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "minecraft");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Linux/Unix
            minecraftDir = Paths.get(System.getProperty("user.home"), ".minecraft");
        } else {
            return null;
        }

        if (!minecraftDir.toFile().exists()) {
            return null;
        }

        return minecraftDir;
    }

    public static void deleteDirectory(File dir) {
        String[] list = dir.list();
        if (list == null)
            return;
        for (String path : list) {
            File file = new File(dir, path);
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                if (!file.delete()) {
                    System.err.println("Couldn't delete file " + file);
                }
            }
        }
        if (!dir.delete()) {
            System.err.println("Couldn't delete directory " + dir);
        }
    }

    public static void copyDirectory(Path dir, Path to) {
        String[] list = dir.toFile().list();
        if (list == null)
            return;
        for (String path : list) {
            Path file = dir.resolve(path);
            Path target = to.resolve(path);
            if (file.toFile().isDirectory()) {
                copyDirectory(file, target);
            } else {
                target.toFile().mkdirs();
                try {
                    Files.copy(file, target, REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
