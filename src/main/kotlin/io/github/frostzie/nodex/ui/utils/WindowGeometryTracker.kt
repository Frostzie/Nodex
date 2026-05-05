package io.github.frostzie.nodex.ui.utils

import io.github.frostzie.nodex.domain.config.WindowBounds
import javafx.stage.Stage

/**
 * A UI utility that tracks the geometry of a JavaFX Stage and gives
 * callbacks when the geometry changes.
 */
class WindowGeometryTracker(
    private val stage: Stage,
    private val onGeometryChanged: (WindowBounds) -> Unit
) {
    init {
        setupListeners()
    }

    private fun setupListeners() {
        // Track position
        stage.xProperty().addListener { _, _, newValue ->
            if (shouldTrack()) onGeometryChanged(getCurrentBounds().copy(x = newValue.toDouble()))
        }
        stage.yProperty().addListener { _, _, newValue ->
            if (shouldTrack()) onGeometryChanged(getCurrentBounds().copy(y = newValue.toDouble()))
        }

        // Track size
        stage.widthProperty().addListener { _, _, newValue ->
            if (shouldTrack()) onGeometryChanged(getCurrentBounds().copy(width = newValue.toDouble()))
        }
        stage.heightProperty().addListener { _, _, newValue ->
            if (shouldTrack()) onGeometryChanged(getCurrentBounds().copy(height = newValue.toDouble()))
        }

        // Track maximized state
        stage.maximizedProperty().addListener { _, _, maximized ->
            onGeometryChanged(getCurrentBounds().copy(isMaximized = maximized))
        }
    }

    /**
     * Prevent tracking while maximized, hidden, or during invalid states (NaN).
     */
    private fun shouldTrack(): Boolean {
        return stage.isShowing && !stage.isMaximized &&
                stage.x.isFinite() && stage.y.isFinite() &&
                stage.width.isFinite() && stage.height.isFinite()
    }

    /**
     * Captures the current bounds of the stage.
     */
    private fun getCurrentBounds(): WindowBounds {
        return WindowBounds(
            x = stage.x,
            y = stage.y,
            width = stage.width,
            height = stage.height,
            isMaximized = stage.isMaximized
        )
    }

    /**
     * Helper to apply bounds to the stage.
     */
    fun applyState(bounds: WindowBounds) {
        stage.width = bounds.width
        stage.height = bounds.height
        stage.isMaximized = bounds.isMaximized

        if (bounds.x != -1.0 && bounds.y != -1.0) {
            stage.x = bounds.x
            stage.y = bounds.y
        } else if (!bounds.isMaximized) {
            stage.centerOnScreen()
        }
    }
}
