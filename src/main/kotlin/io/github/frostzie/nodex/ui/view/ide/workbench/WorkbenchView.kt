package io.github.frostzie.nodex.ui.view.ide.workbench

import io.github.frostzie.nodex.domain.uicontract.EditorTab
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.EditorAreaView
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.CodeEditorView
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.EmptyCodeEditorView
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.DockLayerViewModel
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.editor.EditorAreaViewModel
import javafx.scene.Node
import javafx.scene.layout.StackPane

class WorkbenchView(
    dockLayerViewModel: DockLayerViewModel,
    editorAreaViewModel: EditorAreaViewModel,
    codeEditorViewFactory: (EditorTab) -> CodeEditorView,
    emptyEditorView: EmptyCodeEditorView,
    toolViews: Map<ToolWindow, Node>
) : StackPane() {

    init {
        val editorAreaView = EditorAreaView(
            editorAreaViewModel,
            codeEditorViewFactory,
            emptyEditorView
        )

        val dockLayer = DockLayerView(
            dockLayerViewModel,
            editorAreaView,
            toolViews
        )

        children.add(dockLayer)
    }
}
