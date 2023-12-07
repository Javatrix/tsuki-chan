package com.github.javatrix.kawaiisanbot.event;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class EventHandler extends ListenerAdapter {

    public EventHandler() {
        KawaiiSan.getInstance().getApi().addEventListener(this);
    }

}
