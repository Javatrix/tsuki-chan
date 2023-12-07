package com.github.javatrix.kawaiisanbot.event;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class KawaiiSanMentionEventHandler extends EventHandler {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains(KawaiiSan.getInstance().getUser().getAsMention())) {
            event.getMessage().reply("UwU").queue();
        }
    }
}
