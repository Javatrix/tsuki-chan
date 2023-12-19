/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.music;

import com.github.javatrix.tsukichan.audio.MusicPlayer;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlayCommandExecutor implements SlashCommandExecutor {

    public static final OptionData TITLE_OPTION = new OptionData(OptionType.STRING, "title", "Title of the song to play.", true);

    @Override
    public void process(SlashCommandInteractionEvent context) {
        if (context.getMember().getVoiceState() == null || context.getMember().getVoiceState().getChannel() == null) {
            context.reply("You have to be in a voice channel to use this command.").setEphemeral(true).queue();
            return;
        }
        String title = context.getOption(TITLE_OPTION.getName()).getAsString();
        VoiceChannel channel = context.getMember().getVoiceState().getChannel().asVoiceChannel();
        MusicPlayer.get(channel).queue(title);
        context.reply(title + " was added to the queue!").queue();
    }

}
