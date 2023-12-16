/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/kawaiisanbot/main/LICENSE
 */

package com.github.javatrix.kawaiisanbot.data;

import com.github.javatrix.kawaiisanbot.user.Tempban;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    public GuildData(JsonObject json) {
        addProperty("id", json.get("id").getAsString());
        addProperty("tempbans", json.get("tempbans").getAsJsonArray());
    }
}
