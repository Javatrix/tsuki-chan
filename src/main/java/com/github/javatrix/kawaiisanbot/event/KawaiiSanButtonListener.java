package com.github.javatrix.kawaiisanbot.event;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public abstract class KawaiiSanButtonListener extends KawaiiSanEventHandler {

    private final String buttonId;

    public KawaiiSanButtonListener(String buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getMessage().getAuthor().equals(KawaiiSan.getInstance().getUser()) && event.getButton().getId().equals(buttonId)) {
            kawaiiSanButtonPressed(event);
        }
    }

    public abstract void kawaiiSanButtonPressed(ButtonInteractionEvent event);

}
