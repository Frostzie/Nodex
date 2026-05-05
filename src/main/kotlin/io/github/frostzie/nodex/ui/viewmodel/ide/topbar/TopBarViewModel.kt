package io.github.frostzie.nodex.ui.viewmodel.ide.topbar

import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle

/**
 * ViewModel for the TopBarView.
 */
class TopBarViewModel(
    private val navigationService: Navigation,
    private val workspaceLifecycle: WorkspaceLifecycle,
    private val mainStage: MainStage
) {

    fun openIntro() {
        navigationService.navigateTo(AppScreen.INTRO)
    }

    fun openProjectManager() {
        workspaceLifecycle.closeCurrentProject()
        navigationService.navigateTo(AppScreen.PROJECT_MANAGER)
    }

    fun openSettings() {
        navigationService.showOverlay(OverlayScreen.SETTINGS)
    }

    fun closeApp() {
        mainStage.hide()
    }
}
