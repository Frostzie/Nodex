package io.github.frostzie.nodex.ui

import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.ui.view.layout.IdeLayoutView
import io.github.frostzie.nodex.ui.view.layout.IntroLayoutView
import io.github.frostzie.nodex.ui.view.layout.ProjectManagerLayoutView
import javafx.scene.layout.StackPane

/**
 * Manages switching between different app screens.
 *
 * Observing the [Navigation] and updating its children based on the active [AppScreen].
 */
class ScreenHost(
    private val ideLayoutView: IdeLayoutView,
    private val introLayoutView: IntroLayoutView,
    private val projectManagerLayoutView: ProjectManagerLayoutView,
    navigationService: Navigation
) : StackPane() {

    init {
        navigationService.currentScreen.addListener { _, _, newScreen ->
            updateView(newScreen)
        }

        updateView(navigationService.currentScreen.value)
    }

    private fun updateView(screen: AppScreen?) {
        val view = when (screen) {
            AppScreen.IDE -> ideLayoutView
            AppScreen.PROJECT_MANAGER -> projectManagerLayoutView
            AppScreen.INTRO -> introLayoutView
            else -> introLayoutView
        }
        children.setAll(view)
    }
}
