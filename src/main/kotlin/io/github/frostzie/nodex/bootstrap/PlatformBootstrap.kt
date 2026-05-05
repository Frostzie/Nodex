package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.utils.JavaFXInitializer
import io.github.frostzie.nodex.utils.LoggerProvider

/**
 * Responsible for initializing the UI platform (JavaFX).
 */
object PlatformBootstrap {
    private val logger = LoggerProvider.getLogger("PlatformBootstrap")

    fun start() {
        logger.info("Starting JavaFX Platform...")
        JavaFXInitializer.startup {
            logger.info("JavaFX Platform initialized successfully.")
        }
    }
}