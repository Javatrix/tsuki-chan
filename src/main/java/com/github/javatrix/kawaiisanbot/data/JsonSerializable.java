/*
 * This file is part of Kawaii-San Discord bot project (https://github.com/Javatrix/kawaiisanbot).
 * Copyright (c) 2023 Javatrix.
 */

package com.github.javatrix.kawaiisanbot.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonSerializable {

    private final List<JsonProperty> properties = new ArrayList<>();

    public void addProperty(String key, Object value) {
        addProperty(new JsonProperty(key, value));
    }

    public void addProperty(JsonProperty jsonProperty) {
        properties.add(jsonProperty);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        Gson gson = new Gson();
        for (JsonProperty prop : properties) {
            json.add(prop.key(), gson.toJsonTree(prop.value()));
        }
        return json;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
