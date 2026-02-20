plugins {
	id("mod_conventions_common")
}

dependencies {
	implementation(project(":mod_common"))
}

sourceSets.main {
	val common = project(":mod_common").sourceSets.main.get()
	resources.srcDirs(common.resources)
}
