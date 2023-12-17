plugins {
    `java-gradle-plugin`
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("cuchaz:enigma:1.0.0")
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
    implementation("org.ow2.asm:asm-tree:9.5")
    implementation("com.google.code.gson:gson:2.10.1")
}

gradlePlugin {
    plugins {
        create("revival-gradle") {
            id = "revival-gradle"
            implementationClass = "com.harleylizard.revival.gradle.RevivalPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.harleylizard"
            artifactId = "revival-gradle"
            version = "1.0-SNAPSHOT"
        }
    }
    repositories {
        mavenLocal()
    }
}