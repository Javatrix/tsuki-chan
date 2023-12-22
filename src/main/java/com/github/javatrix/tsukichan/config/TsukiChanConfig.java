/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class TsukiChanConfig {

    public String musicIconUrl = "https://cdn-icons-png.flaticon.com/512/7566/7566380.png";
    public Color musicMessageColor = new Color(232, 1, 117, 255);
    public String defaultTempbanReason = "Ban Hammer has spoken!";

    public TsukiChanConfig() {
    }

    private TsukiChanConfig(Runnable variables) {
        try {
            variables.run();
        } catch (Exception ex) {
            LOGGER.exception(ex);
            LOGGER.error("Parsing config file failed. Defaulting to a new config.");
            throw new JsonParseException("Parsing config failed.");
        }
    }

    public TsukiChanConfig fromConfigFile(File configFile) throws IOException {
        return fromJson(new Gson().fromJson(Files.readString(Path.of(configFile.getAbsolutePath())), JsonObject.class));
    }

    public TsukiChanConfig fromJson(JsonObject config) throws JsonParseException {
        TsukiChanConfig conf;
        try {
            conf = new TsukiChanConfig(() -> {
                musicIconUrl = config.get("music_icon_url").getAsString();
                List<JsonElement> color = config.get("music_icon_url").getAsJsonArray().asList();
                musicMessageColor = new Color(color.get(0).getAsInt(), color.get(1).getAsInt(), color.get(2).getAsInt());
                defaultTempbanReason = config.get("default_tempban_reason").getAsString();
            });
        } catch (JsonParseException ex) {
            conf = new TsukiChanConfig();
        }
        return conf;
    }

}
