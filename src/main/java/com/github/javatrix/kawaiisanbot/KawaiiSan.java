package com.github.javatrix.kawaiisanbot;

import com.github.javatrix.kawaiisanbot.command.CommandManager;
import com.github.javatrix.kawaiisanbot.event.KawaiiSanMentionEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KawaiiSan {

    private static final String TOKEN = System.getenv("KAWAII_SAN_TOKEN");
    private static KawaiiSan instance;

    private JDA api;

    public void start() throws IOException {
        instance = this;
        api = JDABuilder.createDefault(TOKEN).build();
        api.getPresence().setPresence(Activity.playing("Still in development! <3"), false);
        new CommandManager(api);
        initEvents();
    }

    private void initEvents() {
        new KawaiiSanMentionEventListener();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pickRandomAvatar();
            }
        }, 0, 120000);
    }

    public void pickRandomAvatar() {
        List<File> icons = new ArrayList<>();
        for (File f : new File("avatars").listFiles()) {
            if (f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) {
                icons.add(f);
            }
        }
        try {
            Icon avatar = Icon.from(icons.get((int) (Math.random() * icons.size())));
            KawaiiSan.getInstance().getUser().getManager().setAvatar(avatar).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static void main(String[] args) throws IOException {
        new KawaiiSan().start();
    }
}
