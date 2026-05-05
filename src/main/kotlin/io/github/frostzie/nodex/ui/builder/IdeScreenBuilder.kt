package io.github.frostzie.nodex.ui.builder

import io.github.frostzie.nodex.domain.uicontract.EditorTab
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle
import io.github.frostzie.nodex.ui.view.ide.bottombar.BottomBarView
import io.github.frostzie.nodex.ui.view.ide.leftbar.LeftBarView
import io.github.frostzie.nodex.ui.view.ide.overlay.ToolWindowDropOverlayView
import io.github.frostzie.nodex.ui.view.ide.topbar.TopBarView
import io.github.frostzie.nodex.ui.view.ide.workbench.WorkbenchView
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.CodeEditorView
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.EmptyCodeEditorView
import io.github.frostzie.nodex.ui.view.ide.workbench.tree.FileTreeView
import io.github.frostzie.nodex.ui.view.layout.IdeLayoutView
import io.github.frostzie.nodex.ui.viewmodel.ide.bottombar.BottomBarViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.leftbar.LeftBarViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.topbar.TopBarViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.DockLayerViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.editor.EditorAreaViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.tree.FileTreeViewModel

/**
 * Builds the IDE screen layout.
 */
class IdeScreenBuilder(
    private val layoutService: Layout,
    private val navigationService: Navigation,
    private val performanceService: PerformanceMonitor,
    private val fileTreeService: FileTree,
    private val projectRuntimeService: ProjectRuntime,
    private val fileTreePersistence: FileTreePersistence,
    private val editorSession: EditorSession,
    private val workspaceLifecycle: WorkspaceLifecycle,
    private val mainStage: MainStage
) {

    fun build(): IdeLayoutView {
        val dockLayerViewModel = DockLayerViewModel(layoutService)
        val fileTreeViewModel = FileTreeViewModel(fileTreeService, projectRuntimeService, fileTreePersistence)
        val editorAreaViewModel = EditorAreaViewModel(editorSession)
        val leftBarViewModel = LeftBarViewModel(layoutService)
        val topBarViewModel = TopBarViewModel(navigationService, workspaceLifecycle, mainStage)
        val bottomBarViewModel = BottomBarViewModel(performanceService)
        val emptyCodeEditorView = EmptyCodeEditorView()

        val codeEditorViewFactory: (EditorTab) -> CodeEditorView = { tab ->
            CodeEditorView(tab.id, tab.content) { tabId, newContent ->
                editorAreaViewModel.updateContent(tabId, newContent)
            }
        }

        val toolViews = mapOf(
            ToolWindow.FILES to FileTreeView(fileTreeViewModel) { path ->
                editorAreaViewModel.openFile(path)
            }
        )

        val workbenchView = WorkbenchView(
            dockLayerViewModel,
            editorAreaViewModel,
            codeEditorViewFactory,
            emptyCodeEditorView,
            toolViews
        )

        val overlayView = ToolWindowDropOverlayView(dockLayerViewModel.currentDropTarget)

        val leftBarView = LeftBarView(leftBarViewModel)
        val topBarView = TopBarView(topBarViewModel)
        val bottomBarView = BottomBarView(bottomBarViewModel)

        return IdeLayoutView(
            workbenchView,
            overlayView,
            topBarView,
            leftBarView,
            bottomBarView
        )
    }
}
