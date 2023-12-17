package com.harleylizard.revival.deserialiser;

import com.google.gson.*;
import com.harleylizard.revival.Environment;
import com.harleylizard.revival.Modification;

import java.lang.reflect.Type;
import java.util.*;

public final class ModificationDeserialiser implements JsonDeserializer<Modification> {
    @Override
    public Modification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        List<String> url = new ArrayList<>();
        for (JsonElement jsonElement : jsonObject.getAsJsonArray("urls")) {
            url.add(jsonElement.getAsString());
        }

        return new Modification(
                jsonObject.getAsJsonPrimitive("name").getAsString(),
                jsonObject.getAsJsonPrimitive("localised-name").getAsString(),
                jsonObject.getAsJsonPrimitive("description").getAsString(),
                createCredits(jsonObject),
                Collections.unmodifiableList(url),
                createEntryPoints(jsonObject),
                createDependencies(jsonObject)
        );
    }

    private Map<String, Modification.Credit> createCredits(JsonObject jsonObject) {
        Map<String, Modification.Credit> map = new HashMap<>();

        JsonObject credits = jsonObject.getAsJsonObject("credits");
        for (Map.Entry<String, JsonElement> entry : credits.entrySet()) {
            map.put(entry.getKey(), createCredit(entry.getValue().getAsJsonObject()));
        }
        return Collections.unmodifiableMap(map);
    }

    private Modification.Credit createCredit(JsonObject jsonObject) {
        List<String> roles = new ArrayList<>();
        List<String> socials = new ArrayList<>();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("roles")) {
            roles.add(jsonElement.getAsString());
        }
        for (JsonElement jsonElement : jsonObject.getAsJsonArray("socials")) {
            socials.add(jsonElement.getAsString());
        }
        return new Modification.Credit(
                Collections.unmodifiableList(roles),
                Collections.unmodifiableList(socials));
    }

    private Map<Environment, String> createEntryPoints(JsonObject jsonObject) {
        Map<Environment, String> map = new HashMap<>();

        JsonObject entryPoints = jsonObject.getAsJsonObject("entry-points");
        for (Map.Entry<String, JsonElement> entry : entryPoints.entrySet()) {
            map.put(Environment.valueOf(entry.getKey().toUpperCase(Locale.ROOT)), entry.getValue().getAsString());
        }
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> createDependencies(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();

        JsonObject dependencies = jsonObject.getAsJsonObject("dependencies");
        for (Map.Entry<String, JsonElement> entry : dependencies.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Collections.unmodifiableMap(map);
    }
}
