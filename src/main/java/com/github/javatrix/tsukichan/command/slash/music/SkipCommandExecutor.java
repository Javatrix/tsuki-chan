/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.music;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.audio.MusicPlayer;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommandExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) {
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        context.replyEmbeds(createEmbed(MusicPlayer.get(context.getMember().getVoiceState().getChannel().asVoiceChannel()).playNext())).queue();
    }

    private MessageEmbed createEmbed(boolean hasNext) {
        return new EmbedBuilder()
                .setAuthor(hasNext ? "Skipped to the next song!" : "There aren't any songs in the queue. The player will stop now!", null, TsukiChan.getConfig().musicIconUrl)
                .setColor(TsukiChan.getConfig().musicMessageColor)
                .build();
    }

}
