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

import static com.github.javatrix.tsukichan.TsukiChan.LOGGER;

public class TsukiChanAudioLoadResultHandler implements AudioLoadResultHandler {

    private final MusicPlayer player;
    private final boolean playInstantly;

    public TsukiChanAudioLoadResultHandler(MusicPlayer player, boolean playInstantly) {
        this.player = player;
        this.playInstantly = playInstantly;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.trackLoaded(track, playInstantly);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        LOGGER.debug("Loading playlist " + playlist.getName());
        boolean first = true;
        for (AudioTrack track : playlist.getTracks()) {
            player.trackLoaded(track, playInstantly && first);
            first = false;
        }
    }

    @Override
    public void noMatches() {
        player.noMatches();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        LOGGER.exception(exception);
    }
}
