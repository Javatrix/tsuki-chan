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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;
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

    public void queue(String url) {
        playerManager.loadItem(url, new TsukiChanAudioLoadResultHandler(this, false));
        audioManager.openAudioConnection(channel);
    }

    /**
     * @return true if there is another track to play.
     */
    public boolean playNext() {
        scheduler.nextTrack();
        return true;
    }

    public void stop() {
        audioManager.closeAudioConnection();
    }

    public void playInstantly(String url) {
        playerManager.loadItem(url, new TsukiChanAudioLoadResultHandler(this, true));
        audioManager.openAudioConnection(channel);
    }

    void trackLoaded(AudioTrack track, boolean playInstantly) {
        if (playInstantly) {
            player.playTrack(track);
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
}
