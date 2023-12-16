/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

package com.github.javatrix.kawaiisanbot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleUtils {

    public static boolean canAssignRole(Member member, Role role) {
        if (member == null || role == null) {
            throw new IllegalArgumentException("Member and role both cannot be null.");
        }
        int maxPermissionLevel = -1;
        for (Role r : member.getRoles()) {
            maxPermissionLevel = Math.max(maxPermissionLevel, r.getPosition());
        }
        return (member.getPermissions().contains(Permission.MANAGE_ROLES) && maxPermissionLevel > role.getPosition()) || member.isOwner();
    }

}
