/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.music;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.audio.MusicPlayer;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class QueueCommandExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) {
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel().asVoiceChannel();
        AudioTrack current = MusicPlayer.get(voiceChannel).getAudioPlayer().getPlayingTrack();
        List<AudioTrack> queue = MusicPlayer.get(voiceChannel).getQueue().stream().toList();
        context.replyEmbeds(createEmbed(current, queue)).setEphemeral(true).queue();
    }

    private MessageEmbed createEmbed(AudioTrack current, List<AudioTrack> queue) {
        EmbedBuilder embed = new EmbedBuilder().setAuthor("Songs in the queue:", null, TsukiChan.getConfig().musicIconUrl)
                .setColor(TsukiChan.getConfig().musicMessageColor)
                .addField("Currently playing", current.getInfo().title, true);
        int i = 1;
        for (AudioTrack track : queue) {
            embed.addField(i + ".", track.getInfo().title, false);
            i++;
        }
        return embed.build();
    }

}
