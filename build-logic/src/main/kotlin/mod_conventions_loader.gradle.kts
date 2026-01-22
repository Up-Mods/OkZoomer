plugins {
	id("mod_conventions_common")
}

dependencies {
	implementation(project(":mod_common"))
}

sourceSets.main {
	val common = project(":mod_common").sourceSets.main.get()
	java.srcDirs(common.java) // Required in order to bundle everything in, but this will make programming annoying!
	resources.srcDirs(common.resources)
}
