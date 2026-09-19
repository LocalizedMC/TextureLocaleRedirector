pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.firstdark.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.2"
    id("dev.kikugie.loom-back-compat") version "0.3"
}

gradle.beforeProject {
    parent?.extensions?.extraProperties?.let { inherited ->
        if (inherited.has("loom.platform")) {
            extensions.extraProperties["loom.platform"] = inherited["loom.platform"]
        }
    }

    buildscript.repositories.apply {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject, file("versions/settings.json5"))
}

rootProject.name = "TextureLocaleRedirector"
