package io.github.frostzie.nodex.api.navigation

import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * Manages the primary application window lifecycle.
 *
 * @see io.github.frostzie.nodex.services.ui.MainStageService
 */
interface MainStage {

    /**
     * Initializes the service with the primary stage and content.
     */
    fun initialize(primaryStage: Stage, content: Region, scene: Scene)

    /** Shows and focuses the primary window, applying the current screen's policy. */
    fun show()

    /** Hides the primary window (iconifies instead of hiding). */
    fun hide()

    /** Whether the primary window is currently visible. */
    fun isShowing(): Boolean

    /** Whether the primary window is currently minimized. */
    fun isIconified(): Boolean
}