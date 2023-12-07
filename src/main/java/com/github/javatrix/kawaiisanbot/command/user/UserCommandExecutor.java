package com.github.javatrix.kawaiisanbot.command.user;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserCommandExecutor {

    void process(UserContextInteractionEvent context);

}
