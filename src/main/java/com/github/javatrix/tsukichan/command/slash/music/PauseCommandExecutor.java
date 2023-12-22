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

import java.util.concurrent.ExecutionException;

public class PauseCommandExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) throws ExecutionException, InterruptedException {
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        VoiceChannel channel = context.getMember().getVoiceState().getChannel().asVoiceChannel();
        boolean pause = !MusicPlayer.get(channel).getAudioPlayer().isPaused();
        MusicPlayer.get(channel).getAudioPlayer().setPaused(pause);
        context.replyEmbeds(createEmbed(MusicPlayer.get(channel).getScheduler().getCurrentTrack().getInfo(), pause, context.getUser())).queue();
    }

    private MessageEmbed createEmbed(AudioTrackInfo info, boolean paused, User user) {
        return new EmbedBuilder()
                .setAuthor((paused ? "Paused " : "Resumed ") + info.title, null, TsukiChan.getConfig().musicIconUrl)
                .addField("Requested by ", user.getAsMention(), false)
                .setColor(TsukiChan.getConfig().musicMessageColor)
                .build();
    }

}
