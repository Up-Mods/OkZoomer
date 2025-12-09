plugins {
	id("mod_conventions_common")
}

dependencies {
	compileOnly(project(":mod_common"))
}

sourceSets.main {
	val common = project(":mod_common").sourceSets.main.get()
	java.srcDirs(common.java)
	resources.srcDirs(common.resources)
}
