plugins {
	id("mod_conventions_common")
	alias(libs.plugins.moddevgradle)
}

base.archivesName = "ok_zoomer-neo"

java {
	withSourcesJar()

	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
}

repositories {
	exclusiveContent {
		forRepository {
			//maven("https://maven.neoforged.net/releases")
			maven("https://prmaven.neoforged.net/NeoForge/pr3198")
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
	implementation(project(":mod_common")) {
		isTransitive = false
	}

	implementation(libs.wrench.wrapper.api.neoforge)
	jarJar(libs.bundles.wrench.wrapper.neoforge)

	compileOnly(libs.sodium.neoforge)
}

sourceSets.main {
	val common = project(":mod_common").sourceSets.main.get()
	java.srcDirs(common.java) // TODO - *sigh* all of this in the name of patches
	resources.srcDirs(common.resources)
}
