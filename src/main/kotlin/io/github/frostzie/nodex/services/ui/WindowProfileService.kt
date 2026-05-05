package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.domain.registry.ScreenProfiles
import io.github.frostzie.nodex.domain.registry.ToolProfiles
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.domain.uicontract.ToolPolicy
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.domain.uicontract.WindowPolicy

/**
 * Provides window layout policies for screen configs.
 */
class WindowProfileService : WindowProfile {
    override fun getScreenPolicy(screen: AppScreen): WindowPolicy =
        ScreenProfiles.appScreens[screen]
            ?: throw IllegalArgumentException("No profile found for screen: $screen")

    override fun getOverlayPolicy(overlay: OverlayScreen): WindowPolicy =
        ScreenProfiles.overlayScreens[overlay]
            ?: throw IllegalArgumentException("No profile found for overlay: $overlay")

    override fun getToolPolicy(kind: ToolWindow): ToolPolicy =
        ToolProfiles.profiles[kind]
            ?: throw IllegalArgumentException("No profile found for tool window: $kind")

    override fun getAllToolKinds(): List<ToolWindow> =
        ToolProfiles.profiles.keys.toList()
}
