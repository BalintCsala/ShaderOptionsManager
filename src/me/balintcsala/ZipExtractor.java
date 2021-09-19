package me.balintcsala.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipExtractor implements Runnable {

    private final File file;
    private boolean finished = false;
    private int processed = 0;

    public ZipExtractor(File file) {
        this.file = file;
    }

    public int countFiles() {
        int result = 0;
        try {
            ZipFile zipFile = new ZipFile(file);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                if (!entries.nextElement().isDirectory())
                    result++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void run() {

        try {
            File destination = new File("tmp");
            ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];

            while (true) {
                ZipEntry entry = inputStream.getNextEntry();
                if (entry == null)
                    break;
                if (entry.isDirectory())
                    continue;

                File extracted = new File(destination, entry.toString());
                extracted.getParentFile().mkdirs();
                FileOutputStream outputStream = new FileOutputStream(extracted);
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                processed++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getProcessed() {
        return processed;
    }
}
