package io.github.frostzie.nodex.domain.config

import io.github.frostzie.nodex.domain.uicontract.ToolWindow

/**
 * Represents the geometric state of a window (Stage).
 */
data class WindowBounds(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var width: Double = -1.0,
    var height: Double = -1.0,
    var isMaximized: Boolean = false
)

/**
 * Single tool window's layout.
 */
data class ToolWindowConfig(
    var toolType: String = "",
    var anchor: String = "RIGHT",
    var visible: Boolean = true,
    var sizeRatio: Double = 0.25
)

/**
 * Layout config for a project.
 */
data class LayoutConfig(
    /**
     * Window states for different screens, indexed by screen name.
     * Use the name of AppScreen or OverlayScreen as the key.
     */
    var windows: MutableMap<String, WindowBounds> = mutableMapOf(),

    /**
     * Layout of the IDE workbench (Center area).
     */
    var workbench: WorkbenchLayout = WorkbenchLayout()
)

/**
 * Layout configuration specific to the workbench (fileTree).
 */
data class WorkbenchLayout(
    var toolWindows: MutableMap<String, ToolWindowConfig> = mutableMapOf(
        ToolWindow.FILES.name to ToolWindowConfig(ToolWindow.FILES.name, "LEFT", true, 0.25)
    )
)
