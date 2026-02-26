plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.fabric.loom.remap)
}

repositories {
	maven("https://maven.terraformersmc.com/releases")
	maven("https://maven.ladysnake.org/releases")
}

base.archivesName = "ok_zoomer-fabric"

dependencies {
	minecraft(libs.minecraft)
	mappings(loom.layered {
		this.officialMojangMappings()
		this.parchment(libs.parchment)
	})
	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)

	modCompileOnly(libs.modmenu)
	modLocalRuntime(libs.modmenu)

	modCompileOnly(libs.bundles.trinkets)

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
