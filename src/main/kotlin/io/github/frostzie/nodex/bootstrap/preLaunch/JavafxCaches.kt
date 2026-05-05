package io.github.frostzie.nodex.bootstrap.preLaunch

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

/**
 * Manages the cache for downloaded JavaFX native jars.
 *
 * Cache layout inside the game directory:
 *```
 *   <gameDir>/
 *   └── .nodex/
 *       └── javafx-cache/
 *           └── 26/
 *               ├── javafx-base-26-win.jar
 *               └── ...
 *```
 */
object JavafxCaches {

    private const val RELATIVE_CACHE = ".nodex/javafx-cache"

    fun getCacheRoot(): Path = getCacheRoot(FabricLoader.getInstance().gameDir)
    fun getVersionDir(version: String): Path = getVersionDir(getCacheRoot(), version)

    fun isComplete(version: String, modules: List<String>, classifier: String): Boolean =
        isComplete(getCacheRoot(), version, modules, classifier)

    /**
     * Ensures the version directory exists and returns it.
     * Creates intermediate directories if needed.
     */
    fun ensureVersionDir(version: String): Path {
        val dir = getVersionDir(version)
        Files.createDirectories(dir)
        return dir
    }

    /**
     * Returns the expected [Path] for a single classifier jar inside the cache.
     */
    fun jarPath(version: String, module: String, classifier: String): Path =
        getVersionDir(version).resolve("javafx-$module-$version-$classifier.jar")

    internal fun getCacheRoot(gameDir: Path): Path =
        gameDir.resolve(RELATIVE_CACHE)

    internal fun getVersionDir(cacheRoot: Path, version: String): Path =
        cacheRoot.resolve(version)

    internal fun isComplete(
        cacheRoot: Path,
        version: String,
        modules: List<String>,
        classifier: String
    ): Boolean {
        val versionDir = getVersionDir(cacheRoot, version)
        return modules.all { module ->
            Files.exists(versionDir.resolve("javafx-$module-$version-$classifier.jar"))
        }
    }
}
