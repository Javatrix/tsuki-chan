/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot.data;

import com.github.javatrix.kawaiisanbot.user.Tempban;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collection;

public class GuildData extends JsonSerializable {

    public GuildData(Guild guild, Collection<Tempban> tempbans) {
        addProperty("id", guild.getId());
        JsonArray tempbanArray = new JsonArray();
        if (tempbans != null) {
            tempbans.forEach(tempban -> tempbanArray.add(tempban.toJson()));
        }
        addProperty("tempbans", tempbanArray);
    }
}
