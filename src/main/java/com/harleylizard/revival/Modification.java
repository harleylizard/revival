package com.harleylizard.revival;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public final class Modification {
    private final String name;
    private final String localisedName;
    private final String description;
    private final Map<String, Credit> credits;
    private final List<String> urls;
    private final Map<Environment, String> entryPoints;
    private final Map<String, String> dependencies;

    @Data
    public static final class Credit {
        private final List<String> roles;
        private final List<String> socials;
    }
}
