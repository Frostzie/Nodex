package io.github.frostzie.nodex.bootstrap.preLaunch

import io.github.frostzie.nodex.utils.LoggerProvider
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

class JavaFxNativeBootstrapper : PreLaunchEntrypoint {
    private val logger = LoggerProvider.getLogger("JavaFxNativeBootstrapper")

    override fun onPreLaunch() {
        logger.info("JavaFX native bootstrap starting.")
        try {
            JavaFxNativeLoader.ensureNativesAvailable()
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to bootstrap JavaFX native libraries. " +
                        "Deleting <gameDir>/.nodex/javafx-cache/ and restarting may fix a corrupt download.",
                e
            )
        }
    }
}
