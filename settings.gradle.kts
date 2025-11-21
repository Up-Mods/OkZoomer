rootProject.name = "ok-zoomer"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        gradlePluginPortal()
    }
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

include("mod_common")
include("mod_fabric")
include("mod_norge")
