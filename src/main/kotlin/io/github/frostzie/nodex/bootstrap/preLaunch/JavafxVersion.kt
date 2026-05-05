package io.github.frostzie.nodex.bootstrap.preLaunch

// Have to keep this in match with libs.versions.toml and Gradle fx module list.
object JavafxVersion {
    const val VERSION = "26"
    val NATIVE_MODULES = listOf("base", "graphics", "controls", "media", "fxml")
}
