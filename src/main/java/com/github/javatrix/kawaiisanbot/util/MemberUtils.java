/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

package com.github.javatrix.kawaiisanbot.util;

import com.github.javatrix.kawaiisanbot.KawaiiSan;
import com.github.javatrix.kawaiisanbot.user.Tempban;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MemberUtils {

    public static void tempban(Member member, String reason, Date expiration) {
        member.ban(0, TimeUnit.SECONDS).reason(reason).queue();
        KawaiiSan.getInstance().addTempban(new Tempban(member.getId(), member.getGuild(), expiration));
        KawaiiSan.getInstance().saveData();
    }

    public static boolean canModify(Member member, Role role) {
        if (member == null || role == null) {
            throw new IllegalArgumentException("Member and role both cannot be null.");
        }
        return getPermissionLevel(member) > role.getPosition();
    }

    public static boolean canModify(Member modifier, Member member) {
        return getPermissionLevel(modifier) > getPermissionLevel(member);
    }

    public static int getPermissionLevel(Member member) {
        return getHighestRole(member).getPosition();
    }

    public static Role getHighestRole(Member member) {
        Role highest = member.getGuild().getPublicRole();
        for (Role role : member.getRoles()) {
            if (role.getPosition() > highest.getPosition()) {
                highest = role;
            }
        }
        return highest;
    }

}
