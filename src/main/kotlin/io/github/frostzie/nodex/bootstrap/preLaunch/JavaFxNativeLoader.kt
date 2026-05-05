package io.github.frostzie.nodex.bootstrap.preLaunch

import io.github.frostzie.nodex.utils.LoggerProvider
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import java.nio.file.Files

/**
 * Manages the full JavaFX native library bootstrap start.
 */
object JavaFxNativeLoader {
    private val logger = LoggerProvider.getLogger("JavaFxNativeLoader")

    fun ensureNativesAvailable() {
        val platform = PlatformDetector.detect()
        logger.debug("Platform detected: ${platform.osName} / ${platform.osArch} " + "JavaFX classifier '${platform.classifier}'")

        val version = JavafxVersion.VERSION
        val modules = JavafxVersion.NATIVE_MODULES
        val classifier = platform.classifier

        if (JavafxCaches.isComplete(version, modules, classifier)) {
            logger.debug("JavaFX $version natives already cached for '${classifier}', skipping download.")
        } else {
            logger.info("JavaFX $version natives not found in cache, starting download.")
            logger.debug("Downloading ${modules.size} jars from Maven Central.")
            download(version, modules, classifier)
        }

        inject(version, modules, classifier)

        logger.info("JavaFX native bootstrap complete.")
    }

    private fun download(version: String, modules: List<String>, classifier: String) {
        val versionDir = JavafxCaches.ensureVersionDir(version)

        for (module in modules) {
            val destination = versionDir.resolve("javafx-$module-$version-$classifier.jar")

            if (Files.exists(destination)) {
                continue
            }

            MavenDownloader.downloadAndVerify(
                artifactId = "javafx-$module",
                version = version,
                classifier = classifier,
                destination = destination
            )
        }

        logger.info("All downloads complete.")
    }

    private fun inject(version: String, modules: List<String>, classifier: String) {
        for (module in modules) {
            val jarPath = JavafxCaches.jarPath(version, module, classifier)
            val launcher = FabricLauncherBase.getLauncher()
            launcher.addToClassPath(jarPath)
        }
    }
}
