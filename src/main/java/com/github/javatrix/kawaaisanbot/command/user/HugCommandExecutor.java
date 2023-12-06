package com.github.javatrix.kawaaisanbot.command.user;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class HugCommandExecutor implements UserCommandExecutor {

    @Override
    public void process(UserContextInteractionEvent context) {
        context.reply(String.format("Hug for %s!", context.getUser().getEffectiveName())).queue();
    }
}
