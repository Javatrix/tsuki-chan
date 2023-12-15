package com.github.javatrix.kawaiisanbot;

import com.github.javatrix.kawaiisanbot.command.CommandManager;
import com.github.javatrix.kawaiisanbot.event.KawaiiSanMentionEventListener;
import com.github.javatrix.kawaiisanbot.util.logging.LogType;
import com.github.javatrix.kawaiisanbot.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KawaiiSan {

    public static final File DATA_DIRECTORY = new File(".kawaii-san");

    private static String version;
    private static final String TOKEN = System.getenv("KAWAII_SAN_TOKEN");
    private static KawaiiSan instance;

    private JDA api;
    private final Logger logger = new Logger("Kawaii-San");

    public void start(boolean debug) throws InterruptedException {
        logger.setDisabled(LogType.DEBUG, !debug);
        initDataDirectory();
        loadProperties();

        logger.info("Starting {name} " + version);
        instance = this;

        logger.info("Loading JDA.");
        api = JDABuilder.createDefault(TOKEN).build().awaitReady();

        logger.info("Setting up version info.");
        api.getPresence().setActivity(Activity.playing(version));

        logger.info("Initializing commands.");
        new CommandManager(api);

        logger.info("Initializing events.");
        initEvents();
    }

    private void initDataDirectory() {
        if (!DATA_DIRECTORY.exists()) {
            logger.info("Data directory does not exist, so creating it.");
            if (!DATA_DIRECTORY.mkdir()) {
                logger.error("Could not create the data directory. If the bot does not have the permission to do so, please run it again with the required privileges.");
                System.exit(1);
            }
        }
    }

    private void loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("gradle.properties"));
            version = properties.get("botVersion").toString();
        } catch (IOException ex) {
            logger.error("Could not read the properties file, quitting!");
            logger.error(ex.toString());
            System.exit(1);
        }
    }

    private void initEvents() {
        new KawaiiSanMentionEventListener();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    pickRandomAvatar();
                } catch (Exception ex) {
                    logger.error("Changing avatar failed: " + ex);
                }
            }
        }, 0, 5 * 60 * 1000);
    }

    public void pickRandomAvatar() {
        File avatars = new File("avatars");
        if (!avatars.exists()) {
            logger.warning("No avatars directory, the avatars won't change.");
            return;
        }
        List<File> icons = new ArrayList<>();
        for (File f : avatars.listFiles()) {
            if (f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) {
                icons.add(f);
            }
        }
        try {
            Icon avatar = Icon.from(icons.get((int) (Math.random() * icons.size())));
            KawaiiSan.getInstance().getUser().getManager().setAvatar(avatar).queue();
        } catch (IOException e) {
            logger.error("Loading avatar file failed: " + e);
        }
    }

    public static KawaiiSan getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

    public JDA getApi() {
        return api;
    }

    public SelfUser getUser() {
        return api.getSelfUser();
    }

    public Member asMember(Guild guild) {
        Member member = guild.getMember(getUser());
        if (member == null) {
            throw new IllegalStateException("The bot is not present in the specified guild: " + guild);
        }
        return member;
    }

    public Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) throws InterruptedException {
        new KawaiiSan().start(args.length != 0 && args[0].equals("debug"));
    }
}
