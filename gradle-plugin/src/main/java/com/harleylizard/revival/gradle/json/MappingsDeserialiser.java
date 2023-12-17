package com.harleylizard.revival.gradle.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MappingsDeserialiser implements JsonDeserializer<Mappings> {
    @Override
    public Mappings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Map<String, MappingsEntry> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("names").entrySet()) {
            MappingsEntry mappingsEntry = jsonDeserializationContext.deserialize(entry.getValue(), MappingsEntry.class);

            map.put(entry.getKey(), mappingsEntry);
        }

        return new Mappings(
                jsonObject.getAsJsonPrimitive("namespace").getAsString(),
                Collections.unmodifiableMap(map)
        );
    }
}
