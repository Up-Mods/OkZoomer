plugins {
	id("mod_conventions_common")
	alias(libs.plugins.shadow)
	alias(libs.plugins.mod.publish.plugin)
}

base.archivesName = "ok_zoomer"

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

// TODO - Somehow make a source JAR from all of this
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

// TODO - Mod Publish Plugin cannot be used without a port of Hissboom to Gradle
publishMods {
	displayName.set("${version.get()} (Fabric/NeoForge)")
	file = tasks.named<Jar>("jar").get().archiveFile
	modLoaders = listOf("fabric", "neoforge")
	changelog = "To Be Updated"
	type = STABLE

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "4lDrPSXX"
		minecraftVersions.addAll(listOf("26.2"))
	}

	curseforge {
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		projectId = "1465234"
		minecraftVersions.addAll(listOf("26.2"))
		javaVersions.addAll(listOf(JavaVersion.VERSION_25))
		clientRequired = true
		changelogType = "markdown"
	}
}
