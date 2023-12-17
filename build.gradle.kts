plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
    `maven-publish`
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    implementation(project.files("libraries/client.jar"))
    implementation("net.minecraft:launchwrapper:1.5")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.spongepowered:mixin:0.8.5-SNAPSHOT")
    implementation("com.google.guava:guava:32.1.3-jre")
    // implementation("commons-io:commons-io:2.15.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "maven-publish")

    java {
        javaToolchains {
            toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        }
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}