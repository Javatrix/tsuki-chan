/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.util;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.user.Tempban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MemberUtils {

    public static void tempban(Member member, String reason, Date expiration) {
        member.ban(0, TimeUnit.SECONDS).reason(reason).queue();
        TsukiChan.getInstance().addTempban(new Tempban(member.getId(), member.getGuild(), expiration));
        TsukiChan.getInstance().saveData();
    }

    public static boolean canModify(Member member, Role role) {
        if (member == null || role == null) {
            throw new IllegalArgumentException("Member and role both cannot be null.");
        }
        return getPermissionLevel(member) > role.getPosition();
    }

    /**
     * @param modifier The member that tries to modify the other member.
     * @param member The member you are trying to modify
     * @return whether the modifier can modify the member.
     */
    public static boolean canModify(Member modifier, Member member) {
        return getPermissionLevel(modifier) > getPermissionLevel(member) && !member.isOwner();
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
