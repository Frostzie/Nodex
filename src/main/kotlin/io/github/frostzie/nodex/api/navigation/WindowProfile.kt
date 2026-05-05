package io.github.frostzie.nodex.api.navigation

import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.domain.uicontract.ToolPolicy
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.domain.uicontract.WindowPolicy

/**
 * Provides window layout policies for screen configs.
 *
 * @see io.github.frostzie.nodex.services.ui.WindowProfileService
 */
interface WindowProfile {
    /** Returns the profile for a primary [AppScreen]. */
    fun getScreenPolicy(screen: AppScreen): WindowPolicy

    /** Returns the profile for an [OverlayScreen]. */
    fun getOverlayPolicy(overlay: OverlayScreen): WindowPolicy

    /** Returns the profile for a specific [ToolWindow] kind. */
    fun getToolPolicy(kind: ToolWindow): ToolPolicy

    /** Returns all known [ToolWindow] kinds. */
    fun getAllToolKinds(): List<ToolWindow>
}
