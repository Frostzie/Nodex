pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net")
		maven("https://maven.kikugie.dev/snapshots")
	}
}


plugins {
	id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"
	shared {
		versions(/*"1.21.8", "1.21.10",*/ "26.1")
	}
	create(rootProject)
}

rootProject.name = "Nodex"
