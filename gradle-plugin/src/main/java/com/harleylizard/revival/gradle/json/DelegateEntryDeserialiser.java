package com.harleylizard.revival.gradle.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class DelegateEntryDeserialiser implements JsonDeserializer<DelegateEntry> {
    @Override
    public DelegateEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new DelegateEntry(jsonObject.getAsJsonPrimitive("class").getAsString(), null);
    }
}
