package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.ui.view.projectManager.MainAreaView
import io.github.frostzie.nodex.ui.view.projectManager.ProjectManagerTopBarView
import io.github.frostzie.nodex.ui.view.projectManager.RecentListView
import javafx.scene.layout.BorderPane

/**
 * Layout for the Project Manager screen.
 */
class ProjectManagerLayoutView(
    projectManagerTopBarView: ProjectManagerTopBarView,
    recentListView: RecentListView,
    mainAreaView: MainAreaView
) : BorderPane() {

    init {
        top = projectManagerTopBarView
        left = recentListView
        center = mainAreaView
    }
}