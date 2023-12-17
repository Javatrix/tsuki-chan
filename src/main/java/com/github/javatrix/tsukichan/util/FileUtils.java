/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.util;

import com.github.javatrix.tsukichan.TsukiChan;

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
            TsukiChan.LOGGER.exception(ex);
        }
    }

    public static void writeToFile(File location, String name, Object content) {
        location.mkdirs();
        writeToFile(new File(location, name), content);
    }

}
