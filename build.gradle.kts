plugins {
    id("fabric-loom") version "1.15.4"
    id("com.modrinth.minotaur") version "2.9.0"
}

group = "de.fabiexe"
version = "1.3.1"

repositories {
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("com.terraformersmc:modmenu:17.0.0-alpha.1")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.139.4+1.21.11")
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
        archiveBaseName = "ClientSpoofer-1.21.11"
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionName = "$version (1.21.11)"
    versionNumber = "$version-1.21.11"
    versionType = if (version.toString().contains("alpha")) "alpha"
    else if (version.toString().contains("beta")) "beta"
    else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.21.11")
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
