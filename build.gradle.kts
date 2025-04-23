plugins {
    id("java")
    id("dev.architectury.loom") version("1.7-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version "1.9.23"
}

group = "chadlymasterson.safepastures"
version = "1.1.0+1.20.1"

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val api_version: String by project
val kotlin_version: String by project
val cobblemon_version: String by project

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft ("com.mojang:minecraft:$minecraft_version")
    mappings ("net.fabricmc:yarn:$yarn_mappings")
    modImplementation ("net.fabricmc:fabric-loader:$loader_version")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$api_version")
    modImplementation(fabricApi.module("fabric-command-api-v2", "$api_version"))

    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlin_version")
    modImplementation("com.cobblemon:fabric:$cobblemon_version")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}