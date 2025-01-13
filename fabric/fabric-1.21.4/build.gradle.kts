plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
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
}
