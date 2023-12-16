package com.harleylizard.revival;

import com.google.gson.*;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.*;

@Data
public final class Modification {
    private final String name;
    private final String localisedName;
    private final String description;
    private final Map<String, Credit> credits;
    private final List<String> urls;
    private final Map<String, String> entryPoints;
    private final Map<String, String> dependencies;

    @Data
    public static final class Credit {
        private final List<String> roles;
        private final List<String> socials;
    }

    public static final class Deserializer implements JsonDeserializer<Modification> {
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

        private Map<String, Credit> createCredits(JsonObject jsonObject) {
            Map<String, Credit> map = new HashMap<>();

            JsonObject credits = jsonObject.getAsJsonObject("credits");
            for (Map.Entry<String, JsonElement> entry : credits.entrySet()) {
                map.put(entry.getKey(), createCredit(entry.getValue().getAsJsonObject()));
            }
            return Collections.unmodifiableMap(map);
        }

        private Credit createCredit(JsonObject jsonObject) {
            List<String> roles = new ArrayList<>();
            List<String> socials = new ArrayList<>();

            for (JsonElement jsonElement : jsonObject.getAsJsonArray("roles")) {
                roles.add(jsonElement.getAsString());
            }
            for (JsonElement jsonElement : jsonObject.getAsJsonArray("socials")) {
                socials.add(jsonElement.getAsString());
            }
            return new Credit(
                    Collections.unmodifiableList(roles),
                    Collections.unmodifiableList(socials));
        }

        private Map<String, String> createEntryPoints(JsonObject jsonObject) {
            Map<String, String> map = new HashMap<>();

            JsonObject entryPoints = jsonObject.getAsJsonObject("entry-points");
            for (Map.Entry<String, JsonElement> entry : entryPoints.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsString());
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
}
