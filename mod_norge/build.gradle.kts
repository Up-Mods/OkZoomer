plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.moddevgradle)
}

base.archivesName = "ok_zoomer-neo"

repositories {}

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
}
