plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation(project.files("libraries/client.jar"))
    implementation("net.minecraft:launchwrapper:1.5")
    implementation("com.google.code.gson:gson:2.10.1")
    // implementation("commons-io:commons-io:2.15.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    javaToolchains {
    }
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    apply(plugin = "java")


}