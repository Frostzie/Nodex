package io.github.frostzie.nodex.domain.uicontract

data class ToolPolicy(
    val title: String,
    val defaultAnchor: PanelPosition = PanelPosition.RIGHT,
    val defaultSizeRatio: Double = 0.15,
    val defaultVisible: Boolean = true
)
