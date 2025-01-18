pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        gradlePluginPortal()
    }
}

rootProject.name = "ClientSpoofer"

include("common")
include("fabric:fabric-1.20.1")
include("fabric:fabric-1.20.4")
include("fabric:fabric-1.20.6")
include("fabric:fabric-1.21.1")
include("fabric:fabric-1.21.2")
include("fabric:fabric-1.21.3")
include("fabric:fabric-1.21.4")
