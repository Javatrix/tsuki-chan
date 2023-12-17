/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.data;

import com.github.javatrix.tsukichan.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class DataManager {

    private final File dataDirectory;

    public DataManager(File dataDirectory) {
        this.dataDirectory = dataDirectory;
        initDataDirectory();
    }

    private void initDataDirectory() {
        if (!dataDirectory.exists()) {
            LOGGER.info("Data directory does not exist, so creating it.");
            if (!dataDirectory.mkdir()) {
                LOGGER.error("Could not create the data directory. If the process does not have the permission to do so, please run it again with the required privileges.");
                System.exit(1);
            }
        }
    }

    public Properties loadProperties() {
        try {
            Properties properties = new Properties();
            if (new File(dataDirectory, "gradle.properties").exists()) {
                return new Properties();
            }
            properties.load(new FileReader("gradle.properties"));
            return properties;
        } catch (IOException ex) {
            LOGGER.error("Could not read the properties file, quitting!");
            LOGGER.error(ex.toString());
            System.exit(1);
        }
        return new Properties();
    }

    public List<GuildData> loadGuilds() throws IOException {
        File guildsDirectory = new File(dataDirectory, "guilds");
        if (!guildsDirectory.exists() || !guildsDirectory.isDirectory()) {
            return null;
        }

        Gson gson = new Gson();
        List<GuildData> guilds = new ArrayList<>();
        for (File guildFile : guildsDirectory.listFiles()) {
            guilds.add(new GuildData(gson.fromJson(Files.readString(Path.of(guildFile.getAbsolutePath())), JsonObject.class)));
        }
        return guilds;
    }

    public void saveGuilds(Collection<GuildData> guilds) {
        for (GuildData guild : guilds) {
            FileUtils.writeToFile(new File(dataDirectory, "guilds/"), guild.getProperty("id").getAsString() + ".json", guild.toJson());
        }
    }

}
