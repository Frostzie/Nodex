package io.github.frostzie.nodex.domain.registry

import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.domain.uicontract.ToolPolicy
import io.github.frostzie.nodex.domain.uicontract.ToolWindow

/**
 * Static metadata for all tool window profiles.
 */
object ToolProfiles {

    val profiles: Map<ToolWindow, ToolPolicy> = mapOf(
        ToolWindow.FILES to ToolPolicy(
            title = "Files",
            defaultAnchor = PanelPosition.LEFT,
            defaultSizeRatio = 0.25,
            defaultVisible = true
        )
    )
}
