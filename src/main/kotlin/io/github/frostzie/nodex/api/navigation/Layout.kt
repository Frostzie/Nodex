package io.github.frostzie.nodex.api.navigation

import io.github.frostzie.nodex.domain.config.WindowBounds
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import java.nio.file.Path

/**
 * Tracks and provides access to the UI layout state.
 *
 * @see io.github.frostzie.nodex.services.core.LayoutService
 */
interface Layout {
    /** The tool window this layout uses. */
    val toolWindowProvider: ToolWindowProvider

    /** Loads layout state for a specific project. */
    fun loadForProject(projectRoot: Path)

    /** Saves layout state for a specific project. */
    fun saveForProject(projectRoot: Path)

    /** Returns the window state for a screen. */
    fun getWindowState(screen: AppScreen): WindowBounds

    /** Updates the window state for a screen. */
    fun updateWindowState(screen: AppScreen, bounds: WindowBounds)

    /** Returns the window state for an overlay screen. */
    fun getOverlayWindowState(overlay: OverlayScreen): WindowBounds

    /** Updates the window state for an overlay screen. */
    fun updateOverlayWindowState(overlay: OverlayScreen, bounds: WindowBounds)
}
