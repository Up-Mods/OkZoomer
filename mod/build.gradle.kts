plugins {
	id("mod_conventions_common")
	alias(libs.plugins.shadow)
}

base.archivesName = "ok_zoomer-universal"

repositories {
	exclusiveContent {
		forRepository {
			maven("https://maven.fabricmc.net/")
		}
		filter {
			includeGroupAndSubgroups("net.fabricmc")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://maven.neoforged.net/releases")
		}
		filter {
			includeModule("net.neoforged", "neoforge")
		}
	}
}

dependencies {
	implementation(project(":mod_common")) {
		isTransitive = false
	}
	implementation(project(":mod_fabric")) {
		isTransitive = false
	}
	implementation(project(":mod_norge")) {
		isTransitive = false
	}
}

tasks.shadowJar {
	archiveClassifier = ""
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

