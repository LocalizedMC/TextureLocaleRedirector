plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.project.prop("loom.platform")
})

val minecraft: String = stonecutter.current.version

version = "${mod.version}+mc$minecraft"
base.archivesName.set("${mod.id}-common")

loom {
    silentMojangMappingsLicense()

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
}

java {
    withSourcesJar()

    val requiredJava = when {
        stonecutter.current.parsed >= "21.1" -> JavaVersion.VERSION_25
        stonecutter.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }

    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}
