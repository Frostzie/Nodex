package io.github.frostzie.nodex.ui.viewmodel.ide.workbench.editor

import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.domain.uicontract.EditorTab
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import java.nio.file.Path

class EditorAreaViewModel(
    private val editorSession: EditorSession
) {
    val tabs: ObservableList<EditorTab> = editorSession.tabs

    val selectedTab: ReadOnlyObjectProperty<EditorTab?> = editorSession.selectedTab

    fun openFile(path: Path): EditorTab {
        return editorSession.openFile(path)
    }

    fun closeTab(tab: EditorTab) {
        editorSession.closeTab(tab.id, saveBeforeClose = true)
    }

    fun selectTab(tab: EditorTab) {
        editorSession.selectTab(tab.id)
    }

    fun updateContent(tabId: String, newContent: String) {
        editorSession.updateContent(tabId, newContent)
    }
}
