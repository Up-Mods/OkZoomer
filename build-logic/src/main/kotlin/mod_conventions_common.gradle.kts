plugins {
	`java-library`
	`maven-publish`
}

val javaVersion = 21

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

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifactId = base.archivesName.get()
			from(components["java"])
		}
	}

	repositories {
		providers.environmentVariable("MAVEN_UPLOAD_URL").orNull?.let { mavenUrl ->
			maven(mavenUrl) {
				credentials {
					username = providers.environmentVariable("MAVEN_UPLOAD_USERNAME").get()
					password = providers.environmentVariable("MAVEN_UPLOAD_PASSWORD").get()
				}
			}
		}
	}
}
