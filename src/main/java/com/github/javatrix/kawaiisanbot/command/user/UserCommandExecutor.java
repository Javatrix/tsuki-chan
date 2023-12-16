/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

package com.github.javatrix.kawaiisanbot.command.user;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserCommandExecutor {

    void process(UserContextInteractionEvent context);

}
