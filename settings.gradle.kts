pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        gradlePluginPortal()
    }
}

rootProject.name = "ClientSpoofer"

include("common")
include("fabric:fabric-1.21.4")
