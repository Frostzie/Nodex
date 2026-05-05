package io.github.frostzie.nodex.ui.viewmodel.ide.workbench

import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import javafx.beans.property.*
import kotlin.math.abs

/**
 * ViewModel for the DockLayer area of the Workbench.
 * Handles docking logic, tool window states, and drag-and-drop.
 */
class DockLayerViewModel(private val layoutService: Layout) {
    private val toolWindowManager get() = layoutService.toolWindowProvider

    val toolWindowStates get() = toolWindowManager.states

    /**
     * The current potential drop target position.
     * Observed by the DropOverlayView.
     */
    val currentDropTarget: ObjectProperty<PanelPosition?> = SimpleObjectProperty(null)

    /**
     * Translates a tool's normalized size (0.0 to 1.0) into the
     * actual SplitPane divider position.
     */
    fun getEffectiveDividerPosition(toolType: ToolWindow): Double {
        val state = toolWindowManager.states.find { it.toolType == toolType } ?: return 0.25
        return if (state.anchor == PanelPosition.RIGHT || state.anchor == PanelPosition.BOTTOM) {
            1.0 - state.sizeRatio
        } else {
            state.sizeRatio
        }
    }

    /**
     * Updates a tool's size based on a raw divider position from the View.
     */
    fun onDividerMoved(toolType: ToolWindow, rawPosition: Double) {
        val state = toolWindowManager.states.find { it.toolType == toolType } ?: return
        val pos = state.anchor
        val normalizedSize = if (pos == PanelPosition.RIGHT || pos == PanelPosition.BOTTOM) {
            1.0 - rawPosition
        } else {
            rawPosition
        }

        if (abs(state.sizeRatio - normalizedSize) > 0.001) {
            toolWindowManager.setSizeRatio(toolType, normalizedSize)
        }
    }

    /**
     * Calculates the potential drop position based on coordinates.
     */
    fun calculateDropPosition(x: Double, w: Double): PanelPosition? {
        val thresholdW = w * 0.25

        // Left/Right (Full Height)
        if (x < thresholdW) return PanelPosition.LEFT
        if (x > w - thresholdW) return PanelPosition.RIGHT

        return null
    }

    /**
     * Updates the layout state based on a panel drop.
     */
    fun onPanelDropped(toolType: ToolWindow, newPosition: PanelPosition): Boolean {
        toolWindowManager.setAnchor(toolType, newPosition)
        return true
    }

    fun onDragOver(x: Double, w: Double) {
        currentDropTarget.set(calculateDropPosition(x, w))
    }

    fun onDragExited() {
        currentDropTarget.set(null)
    }
}
