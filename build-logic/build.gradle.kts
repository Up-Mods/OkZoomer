plugins	{
	`kotlin-dsl`
}

repositories {
	mavenCentral()
}

kotlin {
	compilerOptions {
		jvmToolchain {
			languageVersion = JavaLanguageVersion.of(25)
		}
	}
}
