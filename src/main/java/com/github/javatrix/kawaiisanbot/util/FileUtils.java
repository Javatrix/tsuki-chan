package com.github.javatrix.kawaiisanbot.util;

import com.github.javatrix.kawaiisanbot.KawaiiSan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    private static void writeToFile(File file, Object content) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content.toString());
            writer.close();
        } catch (IOException ex) {
            KawaiiSan.getInstance().getLogger().exception(ex);
        }
    }

    public static void writeToFile(File location, String name, Object content) {
        location.mkdirs();
        writeToFile(new File(location, name), content);
    }

}
