package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.ui.view.ide.bottombar.BottomBarView
import io.github.frostzie.nodex.ui.view.ide.leftbar.LeftBarView
import io.github.frostzie.nodex.ui.view.ide.overlay.ToolWindowDropOverlayView
import io.github.frostzie.nodex.ui.view.ide.rightbar.RightBarView
import io.github.frostzie.nodex.ui.view.ide.topbar.TopBarView
import io.github.frostzie.nodex.ui.view.ide.workbench.WorkbenchView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane

/**
 * The Main Layout of the IDE.
 * Sets the Static Shell and the Workbench.
 */
class IdeLayoutView(
    workbenchView: WorkbenchView,
    overlayView: ToolWindowDropOverlayView,
    topBarView: TopBarView,
    leftBarView: LeftBarView,
    bottomBarView: BottomBarView,
) : StackPane() {

    init {
        // The Static Shell
        val shell = BorderPane()

        // Top Area (TopBar)
        shell.top = topBarView

        // Left Area (LeftBar)
        shell.left = leftBarView

        // Right Area (RightBar)
        shell.right = RightBarView()

        // Bottom Area (BottomBar)
        shell.bottom = bottomBarView

        // Center Area (Workbench + Overlay Area)
        val workbenchContainer = StackPane(workbenchView, overlayView)
        shell.center = workbenchContainer

        children.addAll(shell)
    }
}
