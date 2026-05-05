package io.github.frostzie.nodex.api.navigation

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.stage.Stage

/**
 * Tracks the overall app focus state.
 *
 * @see io.github.frostzie.nodex.services.ui.FocusService
 */
interface FocusTracker {
    /** Whether any tracked window currently has focus. */
    val isFocused: ReadOnlyBooleanProperty

    /** Snapshot of the last known focus state (thread-safe read). */
    val isFocusedSnapshot: Boolean

    /** Register a stage to be tracked for focus changes. */
    fun trackStage(stage: Stage)
}