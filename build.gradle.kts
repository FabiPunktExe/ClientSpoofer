plugins {
    id("fabric-loom") version "1.13.4"
    id("com.modrinth.minotaur") version "2.8.10"
}

group = "de.fabiexe"
version = "1.2.0"

repositories {
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.10")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.17.2")
    modImplementation("com.terraformersmc:modmenu:16.0.0-rc.1")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.134.1+1.21.10")
    implementation("com.google.code.gson:gson:2.13.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    remapJar {
        archiveBaseName = "ClientSpoofer-1.21.10"
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionName = "$version (1.21.10)"
    versionNumber = "$version-1.21.10"
    versionType = if (version.toString().contains("alpha")) "alpha"
    else if (version.toString().contains("beta")) "beta"
    else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.21.10")
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
