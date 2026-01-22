plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.fabric.loom.remap)
}

repositories {
	maven("https://maven.terraformersmc.com/releases")
}

base.archivesName = "ok_zoomer-fabric"

dependencies {
	minecraft(libs.minecraft)
	mappings(loom.officialMojangMappings())
	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)

	modCompileOnly(libs.modmenu)
	modLocalRuntime(libs.modmenu)

	modImplementation(libs.wrench.wrapper.api.fabric)

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
