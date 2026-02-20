plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.moddevgradle)
}

base.archivesName = "ok_zoomer-neo"

java {
	withSourcesJar()

	if (JavaVersion.current() < JavaVersion.toVersion(25)) {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(25))
		}
	}
}

repositories {
	exclusiveContent {
		forRepository {
			maven("https://maven.neoforged.net/releases")
		}
		filter {
			includeModule("net.neoforged", "neoforge")
		}
	}
}

neoForge {
	version = libs.versions.neoforge.get()

	runs {
		register("client") {
			client()
		}
	}

	mods {
		register("ok_zoomer") {
			sourceSet(sourceSets.main.get())
		}
	}
}

dependencies {
	implementation(libs.wrench.wrapper.api.neoforge)
	jarJar(libs.bundles.wrench.wrapper.neoforge)

	compileOnly(libs.sodium.neoforge)
}
