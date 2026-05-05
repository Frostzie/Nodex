package io.github.frostzie.nodex.ui.utils.extensions

import io.github.frostzie.nodex.domain.config.WindowBounds
import io.github.frostzie.nodex.domain.uicontract.WindowPolicy
import io.github.frostzie.nodex.ui.utils.WindowGeometryTracker
import javafx.scene.layout.HeaderBar
import javafx.scene.layout.Region
import javafx.stage.Stage

/**
 * Shared logic for applying window policies to a JavaFX Stage.
 */
fun Stage.applyBasePolicy(content: Region, policy: WindowPolicy, state: WindowBounds, tracker: WindowGeometryTracker?) {
    this.title = policy.title

    content.minWidth = policy.minWidth
    content.minHeight = policy.minHeight

    this.isResizable = policy.isResizable
    
    policy.headerButtonHeight?.let { height ->
        HeaderBar.setPrefButtonHeight(this, height)
    }

    // Resolve Dimensions:
    // 1. If non-resizable, always use Policy dimensions
    // 2. If resizable but no saved state (width < 0), use Policy dimensions
    // 3. Otherwise, use saved State dimensions
    val resolvedWidth = if (!policy.isResizable || state.width < 0) policy.prefWidth else state.width
    val resolvedHeight = if (!policy.isResizable || state.height < 0) policy.prefHeight else state.height

    tracker?.applyState(state.copy(width = resolvedWidth, height = resolvedHeight))
}
