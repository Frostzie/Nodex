package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.utils.JavaFXInitializer
import io.github.frostzie.nodex.utils.LoggerProvider

/**
 * Responsible for initializing the UI platform (JavaFX).
 */
//TODO: Double check JavaFXInit.
object PlatformBootstrap {
    private val logger = LoggerProvider.getLogger("PlatformBootstrap")

    fun start() {
        System.setProperty("javafx.allowSystemPropertiesAccess", "true")
        System.setProperty("javafx.platform", "desktop")
        System.setProperty("prism.allowhidpi", "false")

        if (JavaFXInitializer.isJavaFXAvailable()) {
            logger.info("Initializing JavaFX platform...")
            // Start the JavaFX toolkit safely so it is ready for UiBootstrap
            JavaFXInitializer.startup {
                JavaFXInitializer.setImplicitExit(false)
                logger.info("JavaFX Platform initialized successfully.")
            }
        } else {
            logger.warn("JavaFX is not available - GUI features will be disabled")
        }
    }
}