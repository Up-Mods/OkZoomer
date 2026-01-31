plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.fabric.loom)
}

repositories {
	maven("https://maven.terraformersmc.com/releases")
}

base.archivesName = "ok_zoomer-fabric"

dependencies {
	minecraft(libs.minecraft)
	implementation(libs.fabric.loader)

	implementation(libs.fabric.api)

	compileOnly(libs.modmenu)
	localRuntime(libs.modmenu)

	compileOnly(libs.sodium.neoforge)

	implementation(libs.wrench.wrapper.api.fabric)

	include(libs.bundles.wrench.wrapper.fabric)
}

loom {
	mods {
		register("ok_zoomer") {
			sourceSet("main")
		}
	}

	runs {
		named("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			runDir("run")
		}
	}
}
