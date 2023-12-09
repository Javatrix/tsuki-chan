package com.github.javatrix.kawaiisanbot.event.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonInteractionRunnable {

    void onClick(ButtonInteractionEvent event);

}
