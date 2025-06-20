plugins {
    id("fabric-loom") version "1.10.5"
    id("com.modrinth.minotaur") version "2.8.7"
}

group = "de.fabiexe"
version = "1.0.4"

repositories {
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.5")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("com.terraformersmc:modmenu:14.0.0-rc.2")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.127.1+1.21.5")
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
        archiveBaseName = "ClientSpoofer-1.21.5"
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionName = "$version (1.21.5)"
    versionNumber = version.toString()
    versionType = if (version.toString().contains("alpha")) "alpha"
    else if (version.toString().contains("beta")) "beta"
    else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.21.5")
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
