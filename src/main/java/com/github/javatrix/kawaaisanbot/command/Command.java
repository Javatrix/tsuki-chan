package com.github.javatrix.kawaaisanbot.command;

import com.github.javatrix.kawaaisanbot.KawaiiSan;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class Command extends ListenerAdapter {

    private final String name, description;

    public Command(String name, String description, OptionData... options) {
        this.name = name;
        this.description = description;
        KawaiiSan.getInstance().getApi().addEventListener(this);
        KawaiiSan.getInstance().getApi().updateCommands().addCommands(Commands.slash(name, description).addOptions(options)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getInteraction().getFullCommandName().equals(name)) {
            execute(event);
        }
    }

    public abstract void execute(SlashCommandInteractionEvent context);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
