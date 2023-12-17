package com.harleylizard.revival.gradle.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public final class DelegateEntryDeserialiser implements JsonDeserializer<DelegateEntry> {
    @Override
    public DelegateEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            return new DelegateEntry(jsonElement.getAsJsonPrimitive().getAsString(), null);
        }
        return null;
    }
}
