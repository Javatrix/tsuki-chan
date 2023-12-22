/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.music;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.audio.MusicPlayer;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayCommandExecutor implements SlashCommandExecutor {

    public static final OptionData TITLE_OPTION = new OptionData(OptionType.STRING, "title", "Title of the song to play.", true);

    @Override
    public void process(SlashCommandInteractionEvent context) throws ExecutionException, InterruptedException {
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        String title = context.getOption(TITLE_OPTION.getName()).getAsString();
        VoiceChannel channel = context.getMember().getVoiceState().getChannel().asVoiceChannel();
        boolean wasPlaying = MusicPlayer.get(channel).getScheduler().getCurrentTrack() == null;
        CompletableFuture<AudioTrackInfo> trackInfo = new CompletableFuture<>();
        new Thread(() -> {
            try {
                AudioTrackInfo info = title.startsWith("http") ? MusicPlayer.get(channel).queuePlaylist(title).get().getInfo() : MusicPlayer.get(channel).queue("ytsearch:" + title).get().getInfo();
                trackInfo.complete(info);
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }).start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        CompletableFuture<String> messageId = new CompletableFuture<>();
        if (!context.getInteraction().isAcknowledged()) {
            context.reply("Downloading the song(s) seems to take a while, please wait while I'm finishing the job!").setEphemeral(true).queue(interactionHook -> messageId.complete(interactionHook.getId()));
            context.getMessageChannel().sendMessageEmbeds(createEmbed(trackInfo.get(), wasPlaying, context.getUser())).queue();
        }
    }

    private MessageEmbed createEmbed(AudioTrackInfo info, boolean playingNow, User sender) {
        return new EmbedBuilder()
                .setAuthor(playingNow ? "Now playing " + info.title : "Queued " + info.title, null, TsukiChan.getConfig().musicIconUrl)
                .addField("Duration", "``" + songDuration(info.length) + "``", true)
                .addField("Requested by", sender.getAsMention(), true)
                .setDescription(playingNow ? "Playing now!" : "Added to the queue! Use /skip to play it now.")
                .setColor(TsukiChan.getConfig().musicMessageColor)
                .setThumbnail(info.artworkUrl)
                .build();
    }

    private String songDuration(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60 % 60;
        long seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
