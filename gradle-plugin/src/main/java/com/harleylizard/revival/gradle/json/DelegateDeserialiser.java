package com.harleylizard.revival.gradle.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DelegateDeserialiser implements JsonDeserializer<Delegate> {
    @Override
    public Delegate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Map<String, DelegateEntry> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("names").entrySet()) {
            DelegateEntry delegateEntry = jsonDeserializationContext.deserialize(entry.getValue(), DelegateEntry.class);

            map.put(delegateEntry.getKlass(), delegateEntry);
        }

        return new Delegate(
                jsonObject.getAsJsonPrimitive("namespace").getAsString(),
                Collections.unmodifiableMap(map)
        );
    }
}
