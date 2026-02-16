package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.ui.view.bottombar.BottomBarView
import io.github.frostzie.nodex.ui.view.leftbar.LeftBarView
import io.github.frostzie.nodex.ui.view.overlay.FileTreeDropOverlayView
import io.github.frostzie.nodex.ui.view.rightbar.RightBarView
import io.github.frostzie.nodex.ui.view.topbar.TopBarView
import io.github.frostzie.nodex.ui.view.workbench.WorkbenchView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane

/**
 * The Main Layout of the IDE.
 * Sets the Static Shell and the Workbench.
 */
class IdeLayoutView(
    private val workbenchView: WorkbenchView,
    private val overlayView: FileTreeDropOverlayView,
    private val leftBarView: LeftBarView,
    private val bottomBarView: BottomBarView,
): StackPane() {

    init {
        // The Static Shell
        val shell = BorderPane()

        // Top Area (TopBar)
        shell.top = TopBarView()

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