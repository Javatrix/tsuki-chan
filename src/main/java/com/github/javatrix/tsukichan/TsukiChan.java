/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan;

import com.github.javatrix.tsukichan.command.CommandManager;
import com.github.javatrix.tsukichan.data.DataManager;
import com.github.javatrix.tsukichan.data.GuildData;
import com.github.javatrix.tsukichan.event.TsukiChanMentionEventListener;
import com.github.javatrix.tsukichan.user.Tempban;
import com.github.javatrix.tsukichan.util.logging.LogType;
import com.github.javatrix.tsukichan.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TsukiChan {

    public static final File DATA_DIRECTORY = new File(".tsuki-chan");
    public static final Logger LOGGER = new Logger(TsukiChan.class.getSimpleName());
    public static final DataManager DATA_MANAGER = new DataManager(DATA_DIRECTORY);

    private static String version;
    private static TsukiChan instance;

    private JDA api;
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(8);
    private final Map<Guild, List<Tempban>> tempbans = new HashMap<>();

    private TsukiChan() {
    }

    public void start(boolean debug) throws InterruptedException, IOException {
        LOGGER.setDisabled(LogType.DEBUG, !debug);
        Properties props = DATA_MANAGER.loadProperties();
        version = props.get("botVersion").toString();

        LOGGER.info("Starting {name} " + version);
        instance = this;

        LOGGER.info("Loading JDA.");
        api = JDABuilder.createDefault(System.getenv("TSUKI_CHAN_TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build().awaitReady();

        LOGGER.info("Setting up version info.");
        api.getPresence().setActivity(Activity.customStatus("\uD83D\uDCA2 " + version));

        LOGGER.info("Initializing commands.");
        new CommandManager(api);

        LOGGER.info("Initializing events.");
        initEvents();

        LOGGER.info("Loading data.");
        loadData();

        LOGGER.info("Setting up auto saving based on .properties.");
        int autosave = 60;
        try {
            autosave = Integer.parseInt(props.get("autosaveSeconds").toString());
        } catch (Exception ignored) {
            LOGGER.warning("Could not read autosave interval from gradle.properties file. Using default one of 60 seconds.");
        }
        LOGGER.info("Autosave interval is set to " + autosave + " seconds.");
        scheduler.scheduleAtFixedRate(this::saveData, 0, autosave, TimeUnit.SECONDS);
    }

    private void loadData() throws IOException {
        for (GuildData guildData : DATA_MANAGER.loadGuilds()) {
            for (JsonElement tempban : guildData.getProperty("tempbans").getAsJsonArray().asList()) {
                JsonObject tempbanData = tempban.getAsJsonObject();
                Guild guild = api.getGuildById(guildData.getProperty("id").getAsString());
                Tempban t = new Tempban(tempbanData.get("user_id").getAsString(),
                        guild,
                        tempbanData.get("expiration_date").getAsLong());
                tempbans.putIfAbsent(guild, new ArrayList<>());
                tempbans.get(guild).add(t);
                scheduleTempbanRevocation(t);
            }
        }
    }

    public void saveData() {
        LOGGER.info("Saving data...");
        List<GuildData> guilds = new ArrayList<>();
        for (Guild guild : tempbans.keySet()) {
            guilds.add(new GuildData(guild, tempbans.get(guild)));
        }
        DATA_MANAGER.saveGuilds(guilds);
        LOGGER.info("Saved.");
    }

    public void scheduleTempbanRevocation(Tempban tempban) {
        long delay = tempban.getExpiration().getTime() - Calendar.getInstance().getTimeInMillis();
        if (tempban.expired()) {
            delay = 0;
        }
        scheduler.schedule(() -> {
            User user = api.retrieveUserById(tempban.getUserId()).complete();
            if (tempban.getGuild().retrieveBan(user).complete() == null) {
                tempbans.get(tempban.getGuild()).remove(tempban);
                return;
            }
            tempban.getGuild().unban(user).queue();
            Invite invite = tempban.getGuild().getRulesChannel().createInvite().complete();
            user.openPrivateChannel().complete().sendMessage(user.getAsMention() + " your temporary ban on " + tempban.getGuild().getName() + " has expired! :smiley: Join back in with this link:\n" + invite.getUrl()).queue();
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void addTempban(Tempban tempban) {
        tempbans.putIfAbsent(tempban.getGuild(), new ArrayList<>());
        tempbans.get(tempban.getGuild()).add(tempban);
        scheduleTempbanRevocation(tempban);
    }

    public static MessageEmbed createTempbanEmbed(User user, String reason, Date expiration) {
        return new EmbedBuilder()
                .setAuthor("Banned" + user.getEffectiveName(), null, user.getAvatarUrl())
                .addField("Reason", reason, false)
                .addField("Expiration Date", TimeFormat.DATE_TIME_LONG.format(expiration.getTime()), false)
                .setColor(Color.RED)
                .build();
    }

    public static MessageEmbed createTempbanEmbed(User user, Guild guild, String reason, Date expiration) {
        return new EmbedBuilder()
                .setAuthor("You have been banned from " + guild.getName() + " \uD83D\uDE41", null, user.getAvatarUrl())
                .addField("Reason", reason, false)
                .addField("Expiration Date", TimeFormat.DATE_TIME_LONG.format(expiration.getTime()), false)
                .setFooter("I will message you when your ban expires so you can join back.")
                .setColor(Color.RED)
                .build();
    }

    private void initEvents() {
        new TsukiChanMentionEventListener();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                pickRandomAvatar();
            } catch (Exception ex) {
                LOGGER.error("Changing avatar failed: " + ex);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    public void pickRandomAvatar() {
        File avatars = new File("avatars");
        if (!avatars.exists()) {
            LOGGER.warning("No avatars directory, the avatars won't change.");
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
            TsukiChan.getInstance().getUser().getManager().setAvatar(avatar).queue();
        } catch (IOException e) {
            LOGGER.error("Loading avatar file failed: " + e);
        }
    }

    public static TsukiChan getInstance() {
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

    public static void main(String[] args) throws Exception {
        new TsukiChan().start(args.length != 0 && args[0].equals("debug"));
    }
}
