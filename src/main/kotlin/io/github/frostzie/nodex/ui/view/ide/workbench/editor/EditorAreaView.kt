package io.github.frostzie.nodex.ui.view.ide.workbench.editor

import io.github.frostzie.nodex.domain.uicontract.EditorTab
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.CodeEditorView
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane.EmptyCodeEditorView
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.editor.EditorAreaViewModel
import javafx.collections.ListChangeListener
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane

class EditorAreaView(
    private val viewModel: EditorAreaViewModel,
    private val codeEditorViewFactory: (EditorTab) -> CodeEditorView,
    private val emptyEditorView: EmptyCodeEditorView
) : BorderPane() {

    private val tabPane = TabPane().apply {
        tabDragPolicy = TabPane.TabDragPolicy.REORDER
        tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
    }

    private val editorContainer = BorderPane()

    private val editorViewCache = mutableMapOf<String, CodeEditorView>()
    private val tabViewsById = mutableMapOf<String, Tab>()

    private var currentTabId: String? = null
    private var isSyncingSelection = false

    init {
        top = tabPane
        center = editorContainer
        styleClass.add("editor-tab")
        styleClass.add("code-editor")

        tabPane.selectionModel.selectedItemProperty().addListener { _, _, selectedTabView ->
            if (isSyncingSelection) {
                return@addListener
            }

            val selectedTab = selectedTabView?.userData as? EditorTab ?: return@addListener
            if (selectedTab.id != viewModel.selectedTab.get()?.id) {
                viewModel.selectTab(selectedTab)
            }
        }

        viewModel.tabs.addListener(ListChangeListener { _ ->
            reconcileTabs()
            showSelectedEditor()
        })

        viewModel.selectedTab.addListener { _, _, _ ->
            syncTabPaneSelectionWithViewModel()
            showSelectedEditor()
        }

        reconcileTabs()
        syncTabPaneSelectionWithViewModel()
        showSelectedEditor()
    }

    private fun reconcileTabs() {
        val modelTabs = viewModel.tabs.toList()
        val modelTabIds = modelTabs.map { it.id }.toSet()

        val removedIds = tabViewsById.keys.filter { it !in modelTabIds }
        removedIds.forEach { removeTabById(it) }

        modelTabs.forEachIndexed { index, tab ->
            val existingTabView = tabViewsById.getOrPut(tab.id) { createTabView(tab) }

            existingTabView.userData = tab
            existingTabView.text = computeTabText(tab)

            if (index >= tabPane.tabs.size) {
                tabPane.tabs.add(existingTabView)
            } else if (tabPane.tabs[index] !== existingTabView) {
                tabPane.tabs.remove(existingTabView)
                tabPane.tabs.add(index, existingTabView)
            }
        }

        if (tabPane.tabs.size > modelTabs.size) {
            val extraTabs = tabPane.tabs.drop(modelTabs.size).toList()
            extraTabs.forEach { tabPane.tabs.remove(it) }
        }
    }

    private fun createTabView(tab: EditorTab): Tab {
        return Tab().apply {
            isClosable = true
            userData = tab
            text = computeTabText(tab)
            setOnCloseRequest { event ->
                event.consume()
                val tabToClose = userData as? EditorTab ?: tab
                viewModel.closeTab(tabToClose)
            }
        }
    }

    private fun syncTabPaneSelectionWithViewModel() {
        val selectedTabId = viewModel.selectedTab.get()?.id
        isSyncingSelection = true
        try {
            if (selectedTabId == null) {
                tabPane.selectionModel.clearSelection()
                return
            }

            val selectedTabView = tabViewsById[selectedTabId]
            if (selectedTabView != null && tabPane.selectionModel.selectedItem !== selectedTabView) {
                tabPane.selectionModel.select(selectedTabView)
            }
        } finally {
            isSyncingSelection = false
        }
    }

    private fun showSelectedEditor() {
        val selectedTab = viewModel.selectedTab.get()

        if (selectedTab == null) {
            editorContainer.center = emptyEditorView
            currentTabId = null
            return
        }

        val tabId = selectedTab.id
        val editorView = editorViewCache.getOrPut(tabId) {
            codeEditorViewFactory(selectedTab)
        }
        editorView.renderContent(selectedTab.content)

        currentTabId = tabId
        editorContainer.center = editorView
    }

    private fun removeTabById(tabId: String) {
        val tabView = tabViewsById.remove(tabId)
        if (tabView != null) {
            tabPane.tabs.remove(tabView)
        }

        editorViewCache.remove(tabId)

        if (currentTabId == tabId) {
            currentTabId = null
        }
    }

    //TODO: Create an actual dirty text area
    private fun computeTabText(tab: EditorTab): String {
        return if (tab.dirty) "${tab.fileName} *" else tab.fileName
    }
}
