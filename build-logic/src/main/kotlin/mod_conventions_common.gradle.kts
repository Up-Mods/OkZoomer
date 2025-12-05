plugins {
	`java-library`
	`maven-publish`
}

val javaVersion = 21

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
			maven("https://maven.parchmentmc.org")
		}
		filter {
			includeGroupAndSubgroups("org.parchmentmc")
		}
	}
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

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifactId = base.archivesName.get()
			from(components["java"])
		}
	}

	repositories {
		val env = System.getenv()
		if (env.contains("MAVEN_UPLOAD_URL")) {
			maven(uri(env["MAVEN_UPLOAD_URL"]!!)) {
				credentials {
					username = env["MAVEN_UPLOAD_USERNAME"]
					password = env["MAVEN_UPLOAD_PASSWORD"]
				}
			}
		}
	}
}
