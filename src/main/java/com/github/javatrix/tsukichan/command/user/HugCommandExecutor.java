/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.user;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class HugCommandExecutor implements UserCommandExecutor {

    @Override
    public void process(UserContextInteractionEvent context) {
        String username = context.getInteraction().getTargetMember().getEffectiveName();
        context.reply(String.format("1 hug(s) for %s!", username))
                .addActionRow(
                        Button.primary("hug", "Hug " + username)
                                .withEmoji(Emoji.fromUnicode("❤"))
                )
                .queue();

        context.getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
                Message message = event.getMessage();
                String content = message.getContentRaw();
                int amount = Integer.parseInt(content.split(" ")[0]);
                message.editMessage((amount + 1) + content.replaceFirst(String.valueOf(amount), "")).queue();
                event.reply(String.format("%s sent a hug!", event.getUser().getEffectiveName())).queue();
            }
        });
    }

}
