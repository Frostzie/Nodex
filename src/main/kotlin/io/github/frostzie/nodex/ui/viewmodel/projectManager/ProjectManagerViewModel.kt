package io.github.frostzie.nodex.ui.viewmodel.projectManager

import io.github.frostzie.nodex.domain.entity.RecentProject
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle
import javafx.collections.ObservableList
import java.nio.file.Path

class ProjectManagerViewModel(
    private val navigationService: Navigation,
    private val workspaceLifecycle: WorkspaceLifecycle,
    private val recentProjectsService: RecentProjects
) {
    private val mainAreaViewModel = MainAreaViewModel(this)
    val recentProjects: ObservableList<RecentProject>
        get() = recentProjectsService.recentProjects

    fun getMainAreaViewModel(): MainAreaViewModel = mainAreaViewModel

    fun onImportProject(path: Path) {
        val opened = workspaceLifecycle.openProject(path)
        navigationService.navigateTo(if (opened) AppScreen.IDE else AppScreen.PROJECT_MANAGER)
    }

    fun onOpenRecentProject(path: Path): Boolean {
        val opened = workspaceLifecycle.openProject(path)
        if (opened) {
            navigationService.navigateTo(AppScreen.IDE)
        } else {
            navigationService.navigateTo(AppScreen.PROJECT_MANAGER)
        }
        return opened
    }
}
