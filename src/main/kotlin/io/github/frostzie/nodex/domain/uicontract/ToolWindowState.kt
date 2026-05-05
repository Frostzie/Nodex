package io.github.frostzie.nodex.domain.uicontract

/**
 * Immutable domain state for a Tool Window.
 */
data class ToolWindowState(
    val toolType: ToolWindow,
    val title: String,
    val visible: Boolean = true,
    val anchor: PanelPosition = PanelPosition.RIGHT,
    val sizeRatio: Double = 0.15
)