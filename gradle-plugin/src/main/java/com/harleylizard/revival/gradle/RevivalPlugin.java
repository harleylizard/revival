package com.harleylizard.revival.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.JavaPlugin;

public final class RevivalPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPluginManager().apply(JavaPlugin.class);

        RepositoryHandler repositories = target.getRepositories();
        repositories.maven(repository -> repository.setUrl("https://maven.fabricmc.net/"));
        repositories.maven(repository -> repository.setUrl("https://libraries.minecraft.net/"));
        repositories.maven(repository -> repository.setUrl("https://repo.spongepowered.org/repository/maven-public/"));

        try {
            Util.apply(target);
        } catch (Exception e) {
            target.getLogger().error(e.getLocalizedMessage());
        }
    }
}
