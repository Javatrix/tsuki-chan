package com.github.javatrix.kawaaisanbot;

import com.github.javatrix.kawaaisanbot.command.Command;
import com.github.javatrix.kawaaisanbot.command.UwUCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.SelfUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KawaiiSan {

    private static final String TOKEN;
    private static KawaiiSan instance;

    private JDA api;

    static {
        try {
            TOKEN = Files.readString(Path.of("secrets/token"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        instance = this;
        api = JDABuilder.createDefault(TOKEN).build();
        api.getPresence().setPresence(OnlineStatus.ONLINE, false);
        new UwUCommand();
    }

    public static KawaiiSan getInstance() {
        return instance;
    }

    public JDA getApi() {
        return api;
    }

    public SelfUser getUser() {
        return api.getSelfUser();
    }

    public static void main(String[] args) {
        new KawaiiSan().start();
    }
}
