package com.harleylizard.revival;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public final class Options {
    private final String fileName;

    public static final class Deserializer implements JsonDeserializer<Options> {
        @Override
        public Options deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Options(json.getAsJsonObject().getAsJsonPrimitive("file-name").getAsString());
        }
    }
}
