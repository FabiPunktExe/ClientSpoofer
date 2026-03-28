plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.minotaur)
}

group = "de.fabiexe"
version = "1.4.0"

repositories {
    maven("https://maven.terraformersmc.com")
    maven("https://maven.nucleoid.xyz") // ModMenu has dependency to Placeholder API
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
    implementation(libs.modmenu)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 25
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to project.version,
                "minecraft_version" to libs.versions.minecraft.get(),
                "modmenu_version" to libs.versions.modmenu.get()
            ))
        }
    }

    jar {
        archiveBaseName = "ClientSpoofer-${libs.versions.minecraft.get()}"
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionName = "$version (${libs.versions.minecraft.get()})"
    versionNumber = "$version-${libs.versions.minecraft.get()}"
    versionType = if (version.toString().contains("alpha")) "alpha"
    else if (version.toString().contains("beta")) "beta"
    else "release"
    uploadFile = tasks.jar.get()
    gameVersions = listOf(libs.versions.minecraft.get())
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
