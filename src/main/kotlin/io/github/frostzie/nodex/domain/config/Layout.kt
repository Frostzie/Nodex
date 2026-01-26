package io.github.frostzie.nodex.domain.config

data class WindowState(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val isMaximized: Boolean
)

data class LayoutConfig(
    var ide: IdeWindowLayout = IdeWindowLayout(),
    var projectManager: ProjectManagerWindowLayout = ProjectManagerWindowLayout(),
    var settings: SettingWindowLayout = SettingWindowLayout()
)
// IDE Layout
data class IdeWindowLayout(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var width: Double = 1200.0,
    var height: Double = 800.0,
    var isMaximized: Boolean = false,
    var workbench: WorkbenchLayout = WorkbenchLayout()
)

data class WorkbenchLayout(
    var sidebarPosition: String = "LEFT",
    var sidebarVisible: Boolean = true,
    var sidebarSize: Double = 0.25
)

// Project Manager Layout
data class ProjectManagerWindowLayout(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var width: Double = 810.0,
    var height: Double = 812.0,
    var isMaximized: Boolean = false
)

// Settings Layout
data class SettingWindowLayout(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var width: Double = 810.0,
    var height: Double = 812.0,
    var isMaximized: Boolean = false
)