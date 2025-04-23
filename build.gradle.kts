plugins {
    id("java")
    id("dev.architectury.loom") version("1.7-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version "1.8.22"
}

group = "chadlymasterson.safepastures"
version = "1.1.0+1.20.1"

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
    minecraft ("com.mojang:minecraft:1.20.1")
    mappings ("net.fabricmc:yarn:1.20.1+build.10")
    modImplementation ("net.fabricmc:fabric-loader:0.16.13")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.5+1.20.1")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.92.5+1.20.1"))

    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.6+kotlin.1.8.22")
    modImplementation("com.cobblemon:fabric:1.5.2+1.20.1")

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