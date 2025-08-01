plugins {
    id("fabric-loom") version "1.11.4"
    id("com.modrinth.minotaur") version "2.8.8"
}

group = "de.fabiexe"
version = "1.1.0"

repositories {
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.8")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("com.terraformersmc:modmenu:15.0.0-beta.3")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.130.0+1.21.8")
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
        archiveBaseName = "ClientSpoofer-1.21.8"
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionName = "$version (1.21.8)"
    versionNumber = version.toString()
    versionType = if (version.toString().contains("alpha")) "alpha"
    else if (version.toString().contains("beta")) "beta"
    else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.21.8")
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
