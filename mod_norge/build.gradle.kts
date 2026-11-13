plugins {
	id("mod_conventions_common")
	alias(libs.plugins.moddevgradle)
	alias(libs.plugins.mod.publish.plugin)
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

publishMods {
	displayName.set("${version.get()} (NeoForge)")
	file = tasks.named<Jar>("jar").get().archiveFile
	modLoaders = listOf("neoforge")
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
