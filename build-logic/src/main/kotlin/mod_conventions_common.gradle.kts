plugins {
	`java-library`
	`maven-publish`
}

val javaVersion = 25

// TODO - Get rid of mc-publish stuff
val curseforgeId = "354047"
val modrinthId = "aXf2OSFU"

version = System.getenv().get("TAG") ?: "0.0.0"
group = "page.langeweile.ok_zoomer"

java {
	withSourcesJar()

	if (JavaVersion.current() < JavaVersion.toVersion(javaVersion)) {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(javaVersion))
		}
	}
}

repositories {
	exclusiveContent {
		forRepository {
			maven("https://maven.uuid.gg/releases")
		}
		filter {
			includeGroup("page.langeweile.wrench_wrapper")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://maven.quiltmc.org/repository/release")
		}
		filter {
			includeGroupAndSubgroups("org.quiltmc")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://maven.caffeinemc.net/releases")
		}
		filter {
			includeGroup("net.caffeinemc")
		}
	}
	mavenCentral()
}

tasks.withType(JavaCompile::class).configureEach {
	options.release.set(javaVersion)
}

tasks.processResources {
	filteringCharset = "UTF-8"

	val version = project.version
	inputs.property("version", version)
	inputs.property("curseforge_id", curseforgeId)
	inputs.property("modrinth_id", modrinthId)

	filesMatching(listOf("fabric.mod.json", "quilt.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
		expand(mapOf(
			"version" to version,
			"curseforge_id" to curseforgeId,
			"modrinth_id" to modrinthId
		))
	}
}
