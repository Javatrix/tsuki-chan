package com.github.javatrix.kawaiisanbot.event;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class KawaiiSanEventListener extends ListenerAdapter {

    public KawaiiSanEventListener() {
        KawaiiSan.getInstance().getApi().addEventListener(this);
    }

}
