package com.harleylizard.revival;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class Resources {

    public InputStream get(String path) throws IOException {
        return requireNonNull(Resources.class.getClassLoader().getResourceAsStream(path));
    }
}
