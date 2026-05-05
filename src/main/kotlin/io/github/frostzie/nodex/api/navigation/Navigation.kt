package io.github.frostzie.nodex.api.navigation

import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import javafx.beans.property.ReadOnlyObjectProperty

/**
 * Manages the global navigation state.
 *
 * Provides the currently active screen and overlay,
 * and allows transitioning between them.
 *
 * @see io.github.frostzie.nodex.services.ui.NavigationService
 */
interface Navigation {
    /** The currently active primary screen. */
    val currentScreen: ReadOnlyObjectProperty<AppScreen>

    /** The primary screen that was active before the current one. */
    val previousScreen: AppScreen

    /** The currently active overlay, or null if no overlay is shown. */
    val activeOverlay: ReadOnlyObjectProperty<OverlayScreen?>

    /** Switch the current screen to a specified [AppScreen]. */
    fun navigateTo(screen: AppScreen)

    /** Open the specified [OverlayScreen]. */
    fun showOverlay(overlay: OverlayScreen)

    /** Close the currently active overlay. */
    fun closeOverlay()
}
