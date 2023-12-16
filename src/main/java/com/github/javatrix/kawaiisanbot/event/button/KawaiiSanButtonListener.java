/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot.event.button;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import com.github.javatrix.kawaiisanbot.event.KawaiiSanEventListener;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class KawaiiSanButtonListener extends KawaiiSanEventListener {

    private String buttonId;
    private final ButtonInteractionRunnable buttonInteractionRunnable;

    private KawaiiSanButtonListener(String buttonId, ButtonInteractionRunnable buttonInteractionRunnable) {
        super();
        this.buttonId = buttonId;
        this.buttonInteractionRunnable = buttonInteractionRunnable;
    }

    private KawaiiSanButtonListener(ButtonInteractionRunnable buttonInteractionRunnable) {
        super();
        this.buttonInteractionRunnable = buttonInteractionRunnable;
    }

    public static void register(String buttonId, ButtonInteractionRunnable onClick) {
        new KawaiiSanButtonListener(buttonId, onClick);
    }

    public static void register(ButtonInteractionRunnable onClick) {
        new KawaiiSanButtonListener(onClick);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getMessage().getAuthor().equals(KawaiiSan.getInstance().getUser()) && (event.getButton().getId().equals(buttonId) || buttonId == null)) {
            buttonInteractionRunnable.onClick(event);
        }
    }

}
