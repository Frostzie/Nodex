package io.github.frostzie.nodex.ui.viewmodel.layout

import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.services.core.LayoutService
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty

/**
 * ViewModel for the IDE Layout.
 */
class IdeLayoutViewModel {

    // Delegate to Service properties
    val sidebarPosition: ObjectProperty<PanelPosition> get() = LayoutService.sidebarPosition
    val sidebarVisible: BooleanProperty get() = LayoutService.sidebarVisible
    val sidebarSize: DoubleProperty get() = LayoutService.sidebarSize

    /**
     * Calculates the potential drop position based on coordinates.
     * Implement sides priority (H-Shape) logic.
     * Returns null if in the dead zone.
     */
    fun calculateDropPosition(x: Double, y: Double, w: Double, h: Double): PanelPosition? {
        val thresholdW = w * 0.25
        val thresholdH = h * 0.25

        // Left/Right (Full Height)
        if (x < thresholdW) return PanelPosition.LEFT
        if (x > w - thresholdW) return PanelPosition.RIGHT

        // Top/Bottom (Remaining Width)
        if (y < thresholdH) return PanelPosition.TOP
        if (y > h - thresholdH) return PanelPosition.BOTTOM

        // Dead Zone
        return null
    }

    /**
     * Updates the layout state based on a panel drop.
     * @return true if the drop was valid and processed.
     */
    fun onPanelDropped(x: Double, y: Double, w: Double, h: Double): Boolean {
        val newPosition = calculateDropPosition(x, y, w, h)
        if (newPosition != null) {
            LayoutService.sidebarPosition.set(newPosition)
            return true
        }
        return false
    }
}
