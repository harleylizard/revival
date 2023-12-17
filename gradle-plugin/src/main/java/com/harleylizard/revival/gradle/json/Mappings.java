package com.harleylizard.revival.gradle.json;

import lombok.Data;

import java.util.Map;

@Data
public final class Mappings {
    private final String namespace;
    private final Map<String, MappingsEntry> map;
}
