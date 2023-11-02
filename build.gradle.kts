@file:Suppress("VulnerableLibrariesLocal") // for now

plugins {
    id("retro.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc"
    }

    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-s01"
    }

    maven("https://jitpack.io") {
        name = "jitpack" // FIXME scope
    }
}

dependencies {
    api("com.github.the-h-team:labyrinth-gui:1.7.3")
    api("com.github.the-h-team:labyrinth-common:1.7.3")
    api("com.github.the-h-team:labyrinth-skulls:1.7.3")
    implementation("com.github.the-h-team:Enterprise:1.7")
    implementation("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.withType<ProcessResources> {
    // fill in placeholders in plugin.yml TODO use placeholders
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    // shadow config (if needed)
}