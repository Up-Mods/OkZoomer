import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("mod_conventions_loader")
	alias(libs.plugins.fabric.loom.remap)
	alias(libs.plugins.mod.publish.plugin)
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
	mappings(loom.officialMojangMappings())
	modImplementation(libs.fabric.loader)

	modImplementation(libs.fabric.api)

	modCompileOnly(libs.modmenu)
	modLocalRuntime(libs.modmenu)

	modCompileOnly(libs.sodium.fabric)

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
			displayName = "Fabric Client"
			generateRunConfig = true
			runDirectory = projectDir.resolve("run")
		}
	}
}

publishMods {
	displayName.set("${version.get()} (Fabric)")
	file = tasks.named<RemapJarTask>("remapJar").get().archiveFile
	modLoaders = listOf("fabric")
	changelog = "To Be Updated"
	type = STABLE

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "aXf2OSFU"
		minecraftVersions.addAll(listOf("1.21.11"))

		optional {
			id = "P7dR8mSH" // Fabric API
		}

		optional {
			id = "mOgUt4GM" // Mod Menu
		}

		optional {
			id = "AANobbMI" // Sodium
		}
	}

	curseforge {
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		projectId = "354047"
		minecraftVersions.addAll(listOf("1.21.11"))
		javaVersions.addAll(listOf(JavaVersion.VERSION_21, JavaVersion.VERSION_25))
		client = true
		changelogType = "markdown"

		optional("fabric-api")
		optional("modmenu")
		optional("sodium")
	}

	github {
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
		repository = providers.environmentVariable("GITHUB_REPOSITORY")
		commitish = providers.environmentVariable("TAG")
	}
}
