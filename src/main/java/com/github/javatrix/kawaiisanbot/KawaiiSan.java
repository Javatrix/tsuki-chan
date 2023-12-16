/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot;

import com.github.javatrix.kawaiisanbot.command.CommandManager;
import com.github.javatrix.kawaiisanbot.data.GuildData;
import com.github.javatrix.kawaiisanbot.event.KawaiiSanMentionEventListener;
import com.github.javatrix.kawaiisanbot.user.Tempban;
import com.github.javatrix.kawaiisanbot.util.FileUtils;
import com.github.javatrix.kawaiisanbot.util.logging.LogType;
import com.github.javatrix.kawaiisanbot.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KawaiiSan {

    public static final File DATA_DIRECTORY = new File(".kawaii-san");

    private static String version;
    private static final String TOKEN = System.getenv("KAWAII_SAN_TOKEN");
    private static KawaiiSan instance;

    private JDA api;
    private final Logger logger = new Logger("Kawaii-San");
    private final Map<Guild, List<Tempban>> tempbans = new HashMap<>();
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(16);

    public void start(boolean debug) throws InterruptedException, IOException {
        logger.setDisabled(LogType.DEBUG, !debug);
        initDataDirectory();
        loadProperties();

        logger.info("Starting {name} " + version);
        instance = this;

        logger.info("Loading JDA.");
        api = JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL).build().awaitReady();

        logger.info("Setting up version info.");
        api.getPresence().setActivity(Activity.playing(version));

        logger.info("Initializing commands.");
        new CommandManager(api);

        logger.info("Initializing events.");
        initEvents();

        logger.info("Loading data.");
        loadData();
    }

    private void loadData() throws IOException {
        loadGuilds();
    }

    public void saveData() {
        saveGuilds();
    }

    private void saveGuilds() {
        for (Guild guild : api.getGuilds()) {
            GuildData data = new GuildData(guild, tempbans.get(guild));
            FileUtils.writeToFile(new File(DATA_DIRECTORY, "guilds/"), guild.getName() + ".json", data.toJson());
        }
    }

    private void loadGuilds() throws IOException {
        File guilds = new File(DATA_DIRECTORY, "guilds");
        if (!guilds.exists() || !guilds.isDirectory()) {
            return;
        }

        Gson gson = new Gson();
        for (File guildFile : guilds.listFiles()) {
            JsonObject guildData = gson.fromJson(Files.readString(Path.of(guildFile.getAbsolutePath())), JsonObject.class);
            Guild guild = api.getGuildById(guildData.get("id").getAsString());
            logger.debug("Loading guild '" + guild.getName() + "'");
            loadTempbans(guild, guildData.get("tempbans").getAsJsonArray().asList());
        }
    }

    private void loadTempbans(Guild guild, List<JsonElement> tempbans) {
        for (JsonElement tempbanData : tempbans) {
            String id = tempbanData.getAsJsonObject().get("user_id").getAsString();
            Date expiration = new Date(tempbanData.getAsJsonObject().get("expiration_date").getAsLong());
            Tempban tempban = new Tempban(id, guild, expiration);
            if (tempban.expired()) {
                System.out.println(id);
                System.out.println(api.retrieveUserById(id).complete().getEffectiveName());
                guild.unban(api.retrieveUserById(id).complete()).queue();
                return;
            }
            this.tempbans.putIfAbsent(guild, new ArrayList<>());
            this.tempbans.get(guild).add(tempban);
            scheduleTempbanRevocation(tempban);
        }
    }

    private void scheduleTempbanRevocation(Tempban tempban) {
        scheduler.schedule(() -> {
            Invite invite = tempban.getGuild().getRulesChannel().createInvite().complete();
            api.retrieveUserById(tempban.getUserId()).complete().openPrivateChannel().complete().sendMessage("You temporary ban on " + tempban.getGuild().getName() + " has expired. Join back in with this link!\n" + invite.getUrl()).queue();
            tempban.getGuild().unban(api.retrieveUserById(tempban.getUserId()).complete()).queue();
        }, tempban.getExpiration().getTime() - Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS);
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
        }, 5 * 60 * 1000, 5 * 60 * 1000);
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

    public void addTempban(Tempban tempban) {
        tempbans.putIfAbsent(tempban.getGuild(), new ArrayList<>());
        tempbans.get(tempban.getGuild()).add(tempban);
        scheduleTempbanRevocation(tempban);
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

    public static void main(String[] args) throws Exception {
        new KawaiiSan().start(args.length != 0 && args[0].equals("debug"));
    }
}
