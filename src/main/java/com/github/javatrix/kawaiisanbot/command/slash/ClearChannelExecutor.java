package com.github.javatrix.kawaiisanbot.command.slash;

import com.github.javatrix.kawaiisanbot.event.button.KawaiiSanButtonListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ClearChannelExecutor implements SlashCommandExecutor {

    public ClearChannelExecutor() {
        KawaiiSanButtonListener.register("clear_channel", (ButtonInteractionEvent event) -> {
            event.reply("Deleting!").setEphemeral(true).queue();
            clearChannel(event.getChannel());
        });
        KawaiiSanButtonListener.register("abort_clear_channel", (ButtonInteractionEvent event) -> {
            event.reply("Ok, aborting action.").setEphemeral(true).queue();
            event.getMessage().delete().queue();
        });
    }

    @Override
    public void process(SlashCommandInteractionEvent context) {
        context.reply("Are you sure you want to delete all messages in this channel? This cannot be undone! (It will also take a while)")
                .addActionRow(
                        Button.danger("clear_channel", "Yes, do it!").withEmoji(Emoji.fromUnicode("⚠")),
                        Button.primary("abort_clear_channel", "No, abort!")
                )
                .setEphemeral(true).queue();
    }

    private void clearChannel(MessageChannelUnion channel) {
        for (Message message : channel.asGuildMessageChannel().getIterableHistory()) {
            message.delete().queue();
        }
    }

}
