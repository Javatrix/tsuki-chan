package com.github.javatrix.kawaiisanbot.util;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import com.github.javatrix.kawaiisanbot.user.Tempban;
import net.dv8tion.jda.api.entities.Member;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MemberUtils {

    public static void tempban(Member member, Date expiration, String reason) {
        member.ban(0, TimeUnit.SECONDS).reason(reason).queue();
        KawaiiSan.getInstance().addTempban(new Tempban(member.getId(), member.getGuild(), expiration));
        KawaiiSan.getInstance().saveData();
    }

}
