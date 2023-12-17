package com.harleylizard.revival.gradle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harleylizard.revival.gradle.json.Delegate;
import com.harleylizard.revival.gradle.json.DelegateDeserialiser;
import com.harleylizard.revival.gradle.json.DelegateEntry;
import com.harleylizard.revival.gradle.json.DelegateEntryDeserialiser;
import cuchaz.enigma.Enigma;
import cuchaz.enigma.ProgressListener;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskAction;
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

public class RemapClientJarTask extends DefaultTask {
    @TaskAction
    public void run() throws IOException {
        Project project = getProject();

        Path jarPath = downloadJar(project);
        createProguardMappings(project, jarPath);
        remapJar();
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

    private void createProguardMappings(Project project, Path jarPath) throws IOException {
        Path path = getPath(project).resolve("proguard.mappings");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Delegate delegate = getDelegate();

            writer.write("#\n");
            for (Map.Entry<String, String> entry : getNames(jarPath).entrySet()) {

            }
            writer.flush();
        }
    }

    private Delegate getDelegate() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Delegate.class, new DelegateDeserialiser())
                .registerTypeAdapter(DelegateEntry.class, new DelegateEntryDeserialiser())
                .create();

        String path = "delegate.json";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(path)))) {
            return gson.fromJson(reader, Delegate.class);
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

                if (!name.endsWith(".class")) {
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

    private void remapJar() throws IOException {
        Enigma enigma = Enigma.create();

        ProgressListener progressListener = new RevivalProgressListener();
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
