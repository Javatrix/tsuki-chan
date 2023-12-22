/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.CompletableFuture;

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class TsukiChanTrackLoadHandler implements AudioLoadResultHandler {

    private final MusicPlayer player;
    private final boolean playInstantly;
    private final CompletableFuture<AudioTrack> loadedTrack;

    public TsukiChanTrackLoadHandler(MusicPlayer player, boolean playInstantly, CompletableFuture<AudioTrack> loadedTrack) {
        this.player = player;
        this.playInstantly = playInstantly;
        this.loadedTrack = loadedTrack;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.trackLoaded(track, playInstantly);
        loadedTrack.complete(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        LOGGER.debug("Loading the first track of playlist " + playlist.getName());
        player.trackLoaded(playlist.getTracks().get(0), playInstantly);
        loadedTrack.complete(playlist.getTracks().get(0));
    }

    @Override
    public void noMatches() {
        player.noMatches();
        loadedTrack.completeExceptionally(new FriendlyException("No matches found!", FriendlyException.Severity.COMMON, null));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LOGGER.exception(exception);
        loadedTrack.completeExceptionally(exception);
    }

}
