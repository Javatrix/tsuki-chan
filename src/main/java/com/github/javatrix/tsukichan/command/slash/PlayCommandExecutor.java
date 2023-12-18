/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash;

import com.github.javatrix.tsukichan.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class PlayCommandExecutor implements SlashCommandExecutor {

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
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler scheduler = new TrackScheduler(player);

        player.addListener(scheduler);
        manager.setSendingHandler(new MusicSender(player));

        manager.openAudioConnection(channel);
        playerManager.loadItem("iuJDhFRDx9M", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                context.reply("Playing " + track.getInfo().title + " by " + track.getInfo().author).queue();
                player.setPaused(false);
                player.startTrack(track, true);
                System.out.println("T: " + player.getPlayingTrack());
                System.out.println("T2: " + player.getPlayingTrack());
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

    private static class MusicSender implements AudioSendHandler {
        private final AudioPlayer audioPlayer;
        private AudioFrame lastFrame;

        public MusicSender(AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
        }

        @Override
        public boolean canProvide() {
            lastFrame = audioPlayer.provide();
            return lastFrame != null;
        }

        @Override
        public ByteBuffer provide20MsAudio() {
            return ByteBuffer.wrap(lastFrame.getData());
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }

}
