package com.harleylizard.revival.gradle.json;

import lombok.Data;

import java.util.Map;

@Data
public final class Delegate {
    private final String namespace;
    private final Map<String, DelegateEntry> map;
}
