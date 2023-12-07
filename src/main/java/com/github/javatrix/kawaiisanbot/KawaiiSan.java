package com.github.javatrix.kawaiisanbot;

import com.github.javatrix.kawaiisanbot.command.CommandManager;
import com.github.javatrix.kawaiisanbot.event.KawaiiSanMentionEventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class KawaiiSan {

    private static final String TOKEN;
    private static KawaiiSan instance;

    static {
        try {
            TOKEN = Files.readString(Path.of("secrets/token"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JDA api;

    public void start() throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        instance = this;
        api = JDABuilder.createDefault(TOKEN).build();
        api.getPresence().setPresence(Activity.playing("/help for help <3"), false);
        new CommandManager(api);
        initEvents();
    }

    private void initEvents() {
        new KawaiiSanMentionEventHandler();
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

    public List<Role> getAssignedRoles(Guild guild) {
        return guild.getRoles().stream().filter(role -> guild.getMembersWithRoles(role).contains(guild.getMember(getUser()))).toList();
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        new KawaiiSan().start();
    }
}
