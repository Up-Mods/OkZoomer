plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.moddevgradle)
}

base.archivesName = "ok_zoomer-neo"

repositories {
	exclusiveContent {
		forRepository {
			// Temporary during pre-release cycle
			maven("https://maven.neoforged.net/releases")
		}
		filter {
			includeModule("net.neoforged", "neoforge")
		}
	}
}

neoForge {
	version = libs.versions.neoforge.get()

	parchment {
		minecraftVersion = libs.versions.minecraft.get()
		mappingsVersion = libs.versions.parchment.get()
	}

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

	runtimeOnly(libs.wrench.wrapper.impl)
}
