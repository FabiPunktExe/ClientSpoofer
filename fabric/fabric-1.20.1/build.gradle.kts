plugins {
    id("fabric-loom") version "1.10.5"
    id("com.modrinth.minotaur") version "2.8.7"
}

repositories {
    maven("https://maven.terraformersmc.com")
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    implementation(project(":common"))
    minecraft("com.mojang:minecraft:1.20.1")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.10")
    modImplementation("com.terraformersmc:modmenu:7.2.2")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.92.5+1.20.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    jar {
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
    versionName = "$version (Fabric 1.20.1)"
    versionNumber = version.toString()
    versionType = if (version.toString().contains("alpha")) "alpha"
                  else if (version.toString().contains("beta")) "beta"
                  else "release"
    uploadFile = tasks.remapJar.get()
    gameVersions = listOf("1.20.1")
    loaders = listOf("fabric")
    dependencies {
        optional.project("modmenu")
    }
}
