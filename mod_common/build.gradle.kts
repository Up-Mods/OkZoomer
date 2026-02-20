plugins {
	id("mod_conventions_common")
	alias(libs.plugins.fabric.loom)
}

base.archivesName = "ok_zoomer-common"

java {
	withSourcesJar()

	if (JavaVersion.current() < JavaVersion.toVersion(25)) {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(25))
		}
	}
}

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
	minecraft(libs.minecraft)
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
