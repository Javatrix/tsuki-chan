/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.event;

import com.github.javatrix.tsukichan.TsukiChan;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TsukiChanMentionEventListener extends TsukiChanEventListener {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains(TsukiChan.getInstance().getUser().getAsMention())) {
            event.getMessage().reply("UwU").queue();
        }
    }

}
