package com.harleylizard.revival.gradle.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MappingsEntryDeserialiser implements JsonDeserializer<MappingsEntry> {
    @Override
    public MappingsEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            return new MappingsEntry(jsonElement.getAsJsonPrimitive().getAsString(), Collections.emptyMap());
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("fields").entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsJsonPrimitive().getAsString());
        }
        return new MappingsEntry(jsonObject.getAsJsonPrimitive("name").getAsString(), Collections.unmodifiableMap(map));
    }
}
