plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow")
    id("com.hypherionmc.modutils.modpublisher")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("common")?.project) {
    "No common project for $project"
}

version = "${mod.version}+mc$minecraft"
base.archivesName.set("${mod.id}-$loader")

architectury {
    platformSetupLoomIde()
    fabric()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentFabric").extendsFrom(commonBundle)
}

loom {
    silentMojangMappingsLicense()

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

repositories {
    maven("https://jitpack.io")
    maven("https://maven.nucleoid.xyz/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${common.mod.dep("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${common.mod.dep("fabric_api")}")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionFabric")) { isTransitive = false }
}

java {
    withSourcesJar()

    val requiredJava = when {
        stonecutter.current.parsed >= "26.1" -> JavaVersion.VERSION_25
        stonecutter.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }

    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    properties(listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.requireProp("mod.mc_dep_fabric")
    )
}

tasks.register<Copy>("buildAndCollect") {
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn(tasks.build)
}

publisher {
    apiKeys {
        modrinth(System.getenv("MODRINTH_TOKEN"))
        curseforge(System.getenv("CURSEFORGE_TOKEN"))
    }

    curseID = common.mod.publish("curseforge")
    modrinthID = common.mod.publish("modrinth")
    versionType = common.mod.publish("version_type")
    changelog = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
    projectVersion = "$loader-${project.version}"
    displayName = "[${loader.upperCaseFirst()}]${project.version}"
    gameVersions = property("mod.mc_targets").toString().split(',')
    loaders = listOf(loader)
    curseEnvironment = common.mod.publish("mod_side")
    artifact = tasks.remapJar.get()
    addAdditionalFile(tasks.remapSourcesJar.get())
}
