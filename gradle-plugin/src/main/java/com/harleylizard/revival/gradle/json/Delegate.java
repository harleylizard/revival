package com.harleylizard.revival.gradle.json;

import lombok.Data;

import java.util.Map;

@Data
public final class Delegate {
    private final String namespace;
    private final Map<String, DelegateEntry> map;

    public boolean contains(String name) {
        for (Map.Entry<String, DelegateEntry> entry : map.entrySet()) {
            if (entry.getValue().getKlass().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Map.Entry<String, DelegateEntry> get(String name) {
        for (Map.Entry<String, DelegateEntry> entry : map.entrySet()) {
            if (entry.getValue().getKlass().equals(name)) {
                return entry;
            }
        }
        throw new RuntimeException(String.format("No delegate entry for %s", name));
    }
}
