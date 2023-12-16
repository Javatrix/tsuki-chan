/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot.user;

import com.github.javatrix.kawaiisanbot.data.JsonSerializable;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Calendar;
import java.util.Date;

public class Tempban extends JsonSerializable {

    private final String userId;
    private final Guild guild;
    private final Date expiration;

    public Tempban(String userId, Guild guild, Date expiration) {
        this.userId = userId;
        this.guild = guild;
        this.expiration = expiration;
        addProperty("user_id", userId);
        addProperty("expiration_date", expiration.getTime());
    }

    public boolean expired() {
        return expiration.before(Calendar.getInstance().getTime());
    }

    public String getUserId() {
        return userId;
    }

    public Guild getGuild() {
        return guild;
    }

    public Date getExpiration() {
        return expiration;
    }
}
