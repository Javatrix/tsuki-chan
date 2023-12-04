package com.github.javatrix.kawaaisanbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KawaiiSan {

    private static final String TOKEN;

    static {
        try {
            TOKEN = Files.readString(Path.of("secrets/token"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(TOKEN).build();
        api.getPresence().setPresence(OnlineStatus.ONLINE, false);
    }

}
