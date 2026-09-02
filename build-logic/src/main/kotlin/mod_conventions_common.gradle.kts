plugins {
	`java-library`
	`maven-publish`
}

val javaVersion = 25

version = System.getenv().get("TAG") ?: "0.0.0"
group = "page.langeweile.ok_zoomer"

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

	filesMatching(listOf("fabric.mod.json", "quilt.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
		expand("version" to version)
	}
}
