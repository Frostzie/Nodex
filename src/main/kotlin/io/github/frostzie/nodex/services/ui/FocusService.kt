package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.Stage
import java.util.WeakHashMap

/**
 * Service responsible for tracking the overall app focus state.
 *
 * Monitors multiple [Stage]s and provides a single [isFocused] property
 * that indicates if any part of the app is currently active.
 */
class FocusService : FocusTracker {
    private val logger = LoggerProvider.getLogger("FocusService")
    private val _isFocused = SimpleBooleanProperty(true)
    override val isFocused: ReadOnlyBooleanProperty = _isFocused

    @Volatile
    private var _isFocusedSnapshot = true
    override val isFocusedSnapshot: Boolean get() = _isFocusedSnapshot

    private var hasEverTrackedAStage = false

    // WeakHashMap to avoid leaking Stages
    private val trackedStages = WeakHashMap<Stage, Boolean>()

    /**
     * Register a stage to be tracked for focus changes.
     */
    override fun trackStage(stage: Stage) {
        check(Platform.isFxApplicationThread()) { "trackStage must be called on the UI thread" }

        hasEverTrackedAStage = true

        // Initial state
        trackedStages[stage] = stage.isFocused
        logger.debug("Tracking stage: {}, initially focused: {}", stage, stage.isFocused)
        updateAppFocus()

        // Listen for changes
        stage.focusedProperty().addListener { _, _, focused ->
            trackedStages[stage] = focused
            logger.debug("Stage focus change: {}, focused: {}", stage, focused)
            updateAppFocus()
        }

        // Remove from tracking when hidden/closed
        stage.showingProperty().addListener { _, _, showing ->
            if (!showing) {
                trackedStages.remove(stage)
                logger.debug("Stage no longer showing, removed from tracking: {}", stage)
                updateAppFocus()
            }
        }
    }

    /**
     * Updates the overall application focus state.
     * If any tracked window is focused, the app is considered focused.
     */
    private fun updateAppFocus() {
        val anyFocusedAndShowing = trackedStages.entries.any { (stage, focused) ->
            stage.isShowing && focused
        }

        val finalFocus = if (!hasEverTrackedAStage) {
            true
        } else {
            anyFocusedAndShowing
        }

        if (_isFocused.get() != finalFocus) {
            _isFocused.set(finalFocus)
            _isFocusedSnapshot = finalFocus
            logger.debug("App focus changed: {}", finalFocus)
        }
    }
}
