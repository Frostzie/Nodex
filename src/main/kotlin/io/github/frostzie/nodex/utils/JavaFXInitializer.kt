package io.github.frostzie.nodex.utils

import javafx.application.Platform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Safely wraps [Platform.startup] to guarantee JavaFX is initialized exactly once.
 */
object JavaFXInitializer {
    private val logger = LoggerProvider.getLogger("JavaFXInitializer")
    private val initialized = AtomicBoolean(false)

    /**
     * Initializes the JavaFX platform if not already started.
     * Executes the [runnable] on the JavaFX Application Thread once ready.
     */
    fun startup(runnable: Runnable) {
        if (initialized.get()) {
            logger.warn("JavaFX Platform already initialized")
            Platform.runLater(runnable)
            return
        }

        try {
            Platform.startup {
                initialized.set(true)
                logger.debug("JavaFX Platform started successfully.")
                runnable.run()
            }
        } catch (_: IllegalStateException) {
            initialized.set(true)
            logger.warn("JavaFX Platform was already initialized by another caller.")
            Platform.runLater(runnable)
        } catch (e: Exception) {
            logger.error("Failed to start JavaFX platform", e)
        }
    }
}
