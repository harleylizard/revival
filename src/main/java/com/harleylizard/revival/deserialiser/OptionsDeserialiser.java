package com.harleylizard.revival.deserialiser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.harleylizard.revival.Options;

import java.lang.reflect.Type;

public final class OptionsDeserialiser implements JsonDeserializer<Options> {
    @Override
    public Options deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Options(json.getAsJsonObject().getAsJsonPrimitive("file-name").getAsString());
    }
}
