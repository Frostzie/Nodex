package io.github.frostzie.nodex.api.navigation

import io.github.frostzie.nodex.domain.config.ToolWindowConfig
import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.domain.uicontract.ToolWindowState
import javafx.collections.ObservableList

/**
 * Manages the collection of Tool window states.
 *
 * @see io.github.frostzie.nodex.services.ui.ToolWindowService
 */
interface ToolWindowProvider {
    /** Current state of all tool windows. */
    val states: ObservableList<ToolWindowState>

    /** Initializes tool windows from saved config. */
    fun initializeFromConfig(configs: Map<String, ToolWindowConfig>)

    /** Saves current tool window states as serializable configs. */
    fun createConfigs(): Map<String, ToolWindowConfig>

    /** Sets visibility of a tool window. */
    fun setVisible(type: ToolWindow, visible: Boolean)

    /** Sets the anchor position of a tool window. */
    fun setAnchor(type: ToolWindow, anchor: PanelPosition)

    /** Sets the size ratio of a tool window. */
    fun setSizeRatio(type: ToolWindow, ratio: Double)
}
