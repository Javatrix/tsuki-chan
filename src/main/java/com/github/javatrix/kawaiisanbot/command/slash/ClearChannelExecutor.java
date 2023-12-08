package com.github.javatrix.kawaiisanbot.command.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class ClearChannelExecutor implements SlashCommandExecutor {

    @Override
    public void process(SlashCommandInteractionEvent context) {
        context.reply("Are you sure you want to delete all messages in this channel? This cannot be undone! (It will also take a while)")
                .addActionRow(
                        Button.danger("clear_channel", "Yes, do it!").withEmoji(Emoji.fromUnicode("⚠")),
                        Button.primary("abort_clear_channel", "No, abort!")
                )
                .queue();
        context.getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
                switch (event.getButton().getId()) {
                    case "clear_channel" -> clearChannel(event.getChannel());
                    case "abort_clear_channel" -> {
                        event.reply("Aborted!").queue();
                        event.getJDA().removeEventListener(this);
                    }
                }
            }
        });
    }

    private void clearChannel(MessageChannelUnion channel) {
        for (Message message : channel.asGuildMessageChannel().getIterableHistory()) {
            message.delete().queue();
        }
    }

}
