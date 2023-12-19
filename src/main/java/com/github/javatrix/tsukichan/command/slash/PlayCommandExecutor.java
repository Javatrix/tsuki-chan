/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash;

import com.github.javatrix.tsukichan.audio.AudioSender;
import com.github.javatrix.tsukichan.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextFilter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class PlayCommandExecutor implements SlashCommandExecutor {

    public static final OptionData TITLE_OPTION = new OptionData(OptionType.STRING, "title", "Title of the song to play.", true);

    @Override
    public void process(SlashCommandInteractionEvent context) {
        Guild guild = context.getGuild();
        AudioManager manager = guild.getAudioManager();
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        VoiceChannel channel = context.getMember().getVoiceState().getChannel().asVoiceChannel();
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration((int) TimeUnit.SECONDS.toMillis(8));
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler scheduler = new TrackScheduler(player);

        player.addListener(scheduler);
        manager.setSendingHandler(new AudioSender(player));

        manager.openAudioConnection(channel);
        playerManager.loadItem(context.getOption(TITLE_OPTION.getName()).getAsString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                context.reply("Playing " + track.getInfo().title + " by " + track.getInfo().author).queue();
                player.startTrack(track, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                context.reply("Sorry, there are no matches for your queue.").setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.exception(exception);
                context.reply("There was an exception loading this track: " + exception).setEphemeral(true).queue();
            }
        });
    }

}
