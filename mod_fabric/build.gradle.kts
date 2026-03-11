plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.fabric.loom)
}

repositories {
	maven("https://maven.terraformersmc.com/releases")
}

base.archivesName = "ok_zoomer-fabric"

java {
	withSourcesJar()

	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
}

dependencies {
	minecraft(libs.minecraft)
	implementation(libs.fabric.loader)

	implementation(libs.fabric.api)

	compileOnly(libs.modmenu)
	//localRuntime(libs.modmenu)

	compileOnly(libs.sodium.neoforge)

	implementation(libs.wrench.wrapper.api.fabric)

	// Instead of bundling both API and Implementation, let's only bundle API
	// The NeoForge distro will bundle Implementation due to JarJar being more finicky than JiJ
	//include(libs.bundles.wrench.wrapper.fabric)
	include(libs.wrench.wrapper.api.fabric)
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
