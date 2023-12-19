/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final MusicPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private AudioTrack currentTrack;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(MusicPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.exception(exception);
        LOGGER.error("Player: " + player);
        LOGGER.error("Track: " + track);
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void queue(AudioTrack track) {
        LOGGER.debug("Queueing " + track.getInfo().title);
        if (!player.getAudioPlayer().startTrack(track, true)) {
            LOGGER.debug("Track " + track.getInfo().title + " added to the queue.");
            queue.offer(track);
            if (currentTrack == null) {
                nextTrack();
            }
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        AudioTrack track = queue.poll();
        if (!player.getAudioPlayer().startTrack(track, false)) {
            LOGGER.debug("Next track is null, stopping the player.");
            player.stop();
            currentTrack = null;
            return;
        }
        currentTrack = track;
        LOGGER.debug("Now playing " + track.getInfo().title);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        LOGGER.debug("Track " + track.getInfo().title + " ended: " + endReason.name());
        currentTrack = null;
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public AudioTrack getCurrentTrack() {
        return currentTrack;
    }
}
