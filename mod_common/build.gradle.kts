plugins {
	id("mod_conventions_common")
	alias(libs.plugins.fabric.loom)
}

base.archivesName = "ok_zoomer-common"

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
	minecraft(libs.minecraft.unobfuscated)
	compileOnly(libs.fabric.loader)
	compileOnly(libs.wrench.wrapper.api.fabric)
}

loom {
	mods {
		register("ok_zoomer") {
			sourceSet("main")
		}
	}
}
