package com.harleylizard.revival.gradle.json;

import lombok.Data;

import java.util.Map;

@Data
public final class DelegateEntry {
    private final String klass;
    private final Map<String, String> fields;
}
