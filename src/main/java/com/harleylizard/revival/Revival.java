package com.harleylizard.revival;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harleylizard.revival.deserialiser.ModificationDeserialiser;
import com.harleylizard.revival.deserialiser.OptionsDeserialiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Revival {
    private final Path mods = Paths.get("mods");

    {
        try {
            loadModifications(Environment.COMMON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadModifications(Environment environment) throws IOException {
        RevivalClassLoader classLoader = new RevivalClassLoader(Revival.class.getClassLoader());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Modification.class, new ModificationDeserialiser())
                .registerTypeAdapter(Options.class, new OptionsDeserialiser())
                .create();

        Options options = options(gson);

        for (Path path : searchFor()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(path, classLoader)) {
                loadClasses(fileSystem, classLoader);

                Modification modification = read(options, gson, fileSystem);

                try {
                    initialise(environment, modification, classLoader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // MixinBootstrap.init();
    }

    private void initialise(Environment environment, Modification modification, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String name = modification.getEntryPoints().get(environment);
        Class<?> klass = classLoader.loadClass(name);
        Constructor<?> constructor = klass.getConstructor();

        Entrypoint entrypoint = (Entrypoint) constructor.newInstance();
    }

    private void loadClasses(FileSystem fileSystem, RevivalClassLoader classLoader) throws IOException {

        try (Stream<Path> stream = Files.walk(fileSystem.getPath("/"))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".class"))) {
            for (Path path : stream.collect(Collectors.toList())) {
                byte[] bytes = Files.readAllBytes(path);

                String name = path.toString();
                name = name.substring(1, name.lastIndexOf(".class")).replace("/", ".");

                classLoader.defineClass(name, bytes);
            }
        }
    }

    private Modification read(Options options, Gson gson, FileSystem fileSystem) throws IOException {
        Path jsonPath = Files.walkFileTree(fileSystem.getPath(options.getFileName()), DefaultVisitor.DEFAULT_VISITOR);

        try (BufferedReader reader = Files.newBufferedReader(jsonPath)) {
            return gson.fromJson(reader, Modification.class);
        }
    }
    
    private List<Path> searchFor() throws IOException {
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

    private Options options(Gson gson) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.get("options.json")))) {
            return gson.fromJson(reader, Options.class);
        }
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

    public static final class RevivalClassLoader extends ClassLoader {

        private RevivalClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
