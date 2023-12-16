package com.harleylizard.revival;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Revival {
    private final Path mods = Paths.get("mods");

    {
        try {
            loadAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadAll() throws IOException {
        ClassLoader classLoader = Revival.class.getClassLoader();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Modification.class, new Modification.Deserializer())
                .create();

        for (Path path : searchForJars()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(path, classLoader)) {
                Modification modification = readJson(gson, fileSystem);
            }
        }
    }

    private Modification readJson(Gson gson, FileSystem fileSystem) throws IOException {
        Path jsonPath = Files.walkFileTree(fileSystem.getPath("modification.json"), new DefaultVisitor() {});

        try (BufferedReader reader = Files.newBufferedReader(jsonPath)) {
            return gson.fromJson(reader, Modification.class);
        }
    }
    
    private List<Path> searchForJars() throws IOException {
        try (Stream<Path> stream = Files.walk(mods, FileVisitOption.FOLLOW_LINKS).filter(path -> ThrowablePredicate.wrap(this::filter).test(path))) {
            return stream.collect(Collectors.toList());
        }
    }

    private boolean filter(Path path) throws IOException {
        if (Files.isRegularFile(path) && Files.size(path) >= 4) {
            byte[] bytes = Files.readAllBytes(path);
            return
                    bytes[0] == 0x50 &&
                    bytes[1] == 0x4B &&
                    bytes[2] == 0x03 &&
                    bytes[3] == 0x04;
        }
        return false;
    }

    @FunctionalInterface
    private interface ThrowablePredicate<J, T extends Throwable> {

        boolean test(J j) throws T;

        static <J, T extends Exception> Predicate<J> wrap(ThrowablePredicate<J, T> predicate) {
            return j -> {
                try {
                    return predicate.test(j);
                } catch (Exception e) {
                    return false;
                }
            };
        }
    }
}
