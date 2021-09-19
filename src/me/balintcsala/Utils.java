package me.balintcsala;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

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
                    System.out.println("Couldn't delete file " + file);
                }
            }
        }
        if (!dir.delete()) {
            System.out.println("Couldn't delete directory " + dir);
        }
    }

}
