package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

/**
 * Manages the global navigation states.
 *
 * Tracks the currently active [AppScreen] and provides a way to
 * transition between primary views.
 * Also manages the active [OverlayScreen].
 * State changes are assigned to the UI thread.
 */
class NavigationService(private val concurrency: Concurrency) : Navigation {

    /**
     * The currently active primary screen.
     * ViewModels should bind to this property to react to navigation changes.
     */
    private val _currentScreen = SimpleObjectProperty(AppScreen.INTRO)
    override val currentScreen: ReadOnlyObjectProperty<AppScreen> = _currentScreen

    /**
     * The primary screen that was active before the current one.
     */
    override var previousScreen: AppScreen = AppScreen.INTRO
        private set

    /**
     * The currently active overlay, or null if no overlay is shown.
     */
    private val _activeOverlay = SimpleObjectProperty<OverlayScreen?>(null)
    override val activeOverlay: ReadOnlyObjectProperty<OverlayScreen?> = _activeOverlay

    /**
     * Switch the current screen to a specified screen.
     */
    override fun navigateTo(screen: AppScreen) {
        concurrency.runOnUI {
            val current = _currentScreen.get()
            if (current != screen) {
                previousScreen = current
                _currentScreen.set(screen)
            }
        }
    }

    /**
     * Open the specified overlay.
     */
    override fun showOverlay(overlay: OverlayScreen) {
        concurrency.runOnUI {
            if (_activeOverlay.get() != overlay) {
                _activeOverlay.set(overlay)
            }
        }
    }

    /**
     * Close the currently active overlay.
     */
    override fun closeOverlay() {
        concurrency.runOnUI {
            if (_activeOverlay.get() != null) {
                _activeOverlay.set(null)
            }
        }
    }
}