/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class MusicPlayer {

    private static final Map<VoiceChannel, MusicPlayer> musicPlayers = new HashMap<>();

    private final VoiceChannel channel;
    private final AudioManager audioManager;
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    public MusicPlayer(VoiceChannel channel) {
        this.channel = channel;
        this.audioManager = channel.getGuild().getAudioManager();

        playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration((int) TimeUnit.SECONDS.toMillis(8));
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(this);
        player.addListener(scheduler);

        audioManager.setSendingHandler(new AudioSender(player));
        audioManager.openAudioConnection(channel);
    }

    public static MusicPlayer get(VoiceChannel channel) {
        if (!musicPlayers.containsKey(channel)) {
            musicPlayers.put(channel, new MusicPlayer(channel));
        }
        return musicPlayers.get(channel);
    }

    public CompletableFuture<AudioTrack> queue(String url) {
        CompletableFuture<AudioTrack> future = new CompletableFuture<>();
        TsukiChanTrackLoadHandler resultHandler = new TsukiChanTrackLoadHandler(this, false, future);
        playerManager.loadItem(url, resultHandler);
        audioManager.openAudioConnection(channel);
        return future;
    }

    public CompletableFuture<AudioTrack> queuePlaylist(String url) {
        CompletableFuture<AudioTrack> future = new CompletableFuture<>();
        TsukiChanPlaylistLoadHandler resultHandler = new TsukiChanPlaylistLoadHandler(this, false, future);
        playerManager.loadItem(url, resultHandler);
        audioManager.openAudioConnection(channel);
        return future;
    }

    /**
     * @return true if there is another track to play.
     */
    public boolean playNext() {
        scheduler.nextTrack();
        return scheduler.currentTrack != null;
    }

    public void stop() {
        audioManager.closeAudioConnection();
    }

    public CompletableFuture<AudioTrack> playInstantly(String url) {
        CompletableFuture<AudioTrack> future = new CompletableFuture<>();
        TsukiChanTrackLoadHandler resultHandler = new TsukiChanTrackLoadHandler(this, true, future);
        playerManager.loadItem(url, resultHandler);
        audioManager.openAudioConnection(channel);
        return future;
    }

    void trackLoaded(AudioTrack track, boolean playInstantly) {
        if (playInstantly || scheduler.getCurrentTrack() == null) {
            player.playTrack(track);
            scheduler.currentTrack = track;
        } else {
            scheduler.queue(track);
        }
    }

    void noMatches() {
        LOGGER.warning("No matches found for the track.");
    }

    public VoiceChannel getChannel() {
        return channel;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public Queue<AudioTrack> getQueue() {
        return scheduler.getQueue();
    }
}
