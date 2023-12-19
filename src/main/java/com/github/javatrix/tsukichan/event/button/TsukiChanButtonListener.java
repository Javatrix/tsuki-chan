/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.event.button;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.event.TsukiChanEventListener;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class TsukiChanButtonListener extends TsukiChanEventListener {

    private String buttonId;
    private final ButtonInteractionRunnable buttonInteractionRunnable;

    private TsukiChanButtonListener(String buttonId, ButtonInteractionRunnable buttonInteractionRunnable) {
        super();
        this.buttonId = buttonId;
        this.buttonInteractionRunnable = buttonInteractionRunnable;
    }

    private TsukiChanButtonListener(ButtonInteractionRunnable buttonInteractionRunnable) {
        super();
        this.buttonInteractionRunnable = buttonInteractionRunnable;
    }

    public static void register(String buttonId, ButtonInteractionRunnable onClick) {
        new TsukiChanButtonListener(buttonId, onClick);
    }

    public static void register(ButtonInteractionRunnable onClick) {
        new TsukiChanButtonListener(onClick);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getMessage().getAuthor().equals(TsukiChan.getInstance().getUser()) && (event.getButton().getId().equals(buttonId) || buttonId == null)) {
            buttonInteractionRunnable.onClick(event);
        }
    }

}
