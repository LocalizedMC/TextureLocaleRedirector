plugins {
    id("dev.kikugie.loom-back-compat")
    id("architectury-plugin")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.project.prop("loom.platform")
})

val minecraft: String = stonecutter.current.version
val requiredJava = when {
    stonecutter.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    stonecutter.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_17
}

version = "${mod.version}+mc$minecraft"
base.archivesName.set("${mod.id}-common")

loom {
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
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
}

java {
    withSourcesJar()

    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.processResources {
    properties(listOf("texturelocaleredirector.mixins.json"),
        "javaCompatibility" to "JAVA_${requiredJava.majorVersion}"
    )
}
