package com.harleylizard.revival.gradle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harleylizard.revival.gradle.json.Mappings;
import com.harleylizard.revival.gradle.json.MappingsDeserialiser;
import com.harleylizard.revival.gradle.json.MappingsEntry;
import com.harleylizard.revival.gradle.json.MappingsEntryDeserialiser;
import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.classprovider.ClasspathClassProvider;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import lombok.experimental.UtilityClass;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.objectweb.asm.ClassReader;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public final class Util {

    public void apply(Project project) throws IOException, MappingParseException {
        Path jarPath = downloadJar(project);
        Path mappingsPath = createProguardMappings(project, jarPath);

        remapJar(project,
                jarPath,
                mappingsPath);
    }

    private Path downloadJar(Project project) throws IOException {
        Path path = getPath(project).resolve("client.jar");

        Files.deleteIfExists(path);

        URL url = new URL("https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar");
        try (InputStream inputStream = url.openStream(); DataOutputStream dataOutputStream = new DataOutputStream(Files.newOutputStream(path))) {
            byte[] bytes = new byte[1024];

            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes, 0, read);
            }
            dataOutputStream.flush();
            return path;
        }
    }

    private Path createProguardMappings(Project project, Path jarPath) throws IOException {
        Path path = getPath(project).resolve("proguard.mappings");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Mappings mappings = getDelegate();
            Map<String, MappingsEntry> map = mappings.getMap();

            writer.write("#\n");
            for (Map.Entry<String, String> entry : getNames(jarPath).entrySet()) {
                String name = entry.getKey();

                if (!map.containsKey(name)) {
                    String namespace = mappings.getNamespace();

                    writer.write(String.format("%s.%s -> %s:\n", namespace, name, name));
                    continue;
                }

                MappingsEntry mappingsEntry = map.get(name);

                String formatted = String.format("%s -> %s:\n", mappingsEntry.getName(), name);
                writer.write(formatted);

                Map<String, String> fields = mappingsEntry.getFields();
                if (!fields.isEmpty()) {
                    for (Map.Entry<String, String> fieldEntry : fields.entrySet()) {
                        String[] split = fieldEntry.getValue().split(":", 2);

                        formatted = String.format("    %s %s -> %s\n", split[0], split[1], fieldEntry.getKey());

                        writer.write(formatted);
                    }
                }
            }
            writer.flush();
            return path;
        }
    }

    private Mappings getDelegate() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Mappings.class, new MappingsDeserialiser())
                .registerTypeAdapter(MappingsEntry.class, new MappingsEntryDeserialiser())
                .create();

        String path = "mappings.json";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(path)))) {
            return gson.fromJson(reader, Mappings.class);
        }
    }

    private InputStream getInputStream(String path) throws IOException {
        return RevivalPlugin.class.getClassLoader().getResource(path).openStream();
    }

    private Map<String, String> getNames(Path jarPath) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(jarPath))) {
            Map<String, String> map = new HashMap<>();

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String name = zipEntry.getName();

                if (!name.endsWith(".class") || name.contains("/")) {
                    continue;
                }

                ClassReader classReader = new ClassReader(readBytes(zipInputStream));

                map.put(classReader.getClassName(), classReader.getSuperName());
            }

            return Collections.unmodifiableMap(map);
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];

            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, read);
            }

            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void remapJar(Project project, Path jarPath, Path mappingsPath) throws IOException, MappingParseException {
        Enigma enigma = Enigma.create();

        ProgressListener progressListener = new RevivalProgressListener();

        EnigmaProject enigmaProject = enigma.openJar(jarPath, new ClasspathClassProvider(), progressListener);

        EntryTree<EntryMapping> entryTree = MappingFormat.PROGUARD.read(mappingsPath, progressListener, enigma
                .getProfile()
                .getMappingSaveParameters());
        enigmaProject.setMappings(entryTree);

        Path path = getPath(project).resolve("client-remapped.jar");

        EnigmaProject.JarExport jar = enigmaProject.exportRemappedJar(progressListener);
        jar.write(path, progressListener);

        project.getDependencies().add("implementation", project.files(path));
    }

    private Path getPath(Project project) throws IOException {
        Path path = directoryToPath(project.getLayout().getBuildDirectory().get()).resolve("revival");
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    private Path directoryToPath(Directory directory) {
        return Paths.get(directory.toString());
    }

    private static final class RevivalProgressListener implements ProgressListener {

        @Override
        public void init(int i, String s) {
        }

        @Override
        public void step(int i, String s) {
        }
    }
}
