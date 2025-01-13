plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.8.7"
}

repositories {
    maven("https://maven.terraformersmc.com")
}

dependencies {
    implementation(project(":common"))
    minecraft("com.mojang:minecraft:1.21.4")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.114.2+1.21.4")
    modImplementation("com.terraformersmc:modmenu:13.0.0")
}

loom {
    accessWidenerPath = file("src/main/resources/clientspoofer.accesswidener")
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

    jar {
        doFirst {
            copy {
                from(rootProject.files("icon.png"))
                into(layout.buildDirectory.dir("resources/main/assets/clientspoofer"))
            }
        }
    }

    remapJar {
        archiveBaseName = "ClientSpoofer-${project.name}"
        doLast {
            copy {
                from(archiveFile)
                into(rootProject.file("build/libs"))
            }
        }
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "nWJHVhGM"
    versionNumber = version.toString()
    versionType = if (version.toString().contains("alpha")) "alpha"
                  else if (version.toString().contains("beta")) "beta"
                  else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.21.4")
    loaders = listOf("fabric")
}
