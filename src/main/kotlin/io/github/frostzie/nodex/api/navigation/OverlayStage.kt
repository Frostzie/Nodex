package io.github.frostzie.nodex.api.navigation

import javafx.stage.Stage

/**
 * Manages modal overlay windows (Overlays).
 *
 * Observes the [Navigation] and creates/shows/hides separate
 * JavaFX Stages for each overlay screen.
 *
 * @see io.github.frostzie.nodex.services.ui.OverlayStageService
 */
interface OverlayStage {

    /**
     * Sets the owner stage for overlay windows.
     */
    fun setPrimaryStage(stage: Stage)

    /**
     * Starts observing overlay navigation changes.
     */
    fun initialize()
}