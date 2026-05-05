package io.github.frostzie.nodex.domain.uicontract

/**
 * Defines the behavioral for a window (Stage).
 */
data class WindowPolicy(
    val title: String,
    val minWidth: Double = 600.0,
    val minHeight: Double = 400.0,
    val prefWidth: Double = 1200.0,
    val prefHeight: Double = 800.0,
    val isResizable: Boolean = true,
    val isPersistent: Boolean = true,
    val isModal: Boolean = false,
    val alwaysOnTop: Boolean = false,
    val headerButtonHeight: Double? = null
)
