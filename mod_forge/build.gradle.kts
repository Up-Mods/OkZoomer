plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.moddevgradle)
}

base.archivesName = "ok_zoomer-forge"

repositories {
	exclusiveContent {
		forRepository {
			maven("https://maven.minecraftforge.net/")
		}
		filter {
			includeModule("net.minecraftforge", "forge")
		}
	}
}

legacyForge {
	version = libs.versions.forge.get()

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
	annotationProcessor(libs.mixin)
	annotationProcessor(libs.mixinextras.common)

	implementation(libs.wrench.wrapper.api.forge)
	jarJar(libs.bundles.wrench.wrapper.forge)

	runtimeOnly(libs.wrench.wrapper.impl)

	compileOnly(libs.mixinextras.common)
	implementation(libs.mixinextras.forge)
	// Why is MixinExtras shadowing ANTLR, Apache Commons and Gson???
	jarJar(variantOf(libs.mixinextras.forge) {
		classifier("slim")
	})
}

mixin {
	add(sourceSets.main.get(), "ok_zoomer_forge.mixins.refmap.json")
	config("ok_zoomer_forge.mixins.json")
}

tasks.jar {
	manifest {
		attributes(
			"MixinConfigs" to "ok_zoomer_forge.mixins.json"
		)
	}
}
