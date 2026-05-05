package io.github.frostzie.nodex.ui.view.settings

import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryViewModel
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

/**
 * Hosts settings panels and swaps them based on selected category.
 *
 * @param categoryViewModel ViewModel for category selection and search.
 * @param panels Map of panel ID to panel Node.
 */
class SettingsContentHostView(
    private val categoryViewModel: SettingsCategoryViewModel,
    panels: Map<String, Node>,
    emptyView: Node = Label("Select a category")
) : StackPane() {
    private val panelMap = panels
    private val noResultsView = Label("No results found").apply {
        isVisible = false
        isManaged = false
    }
    private val noSelectionView = emptyView.apply {
        isVisible = false
        isManaged = false
    }

    init {
        alignment = Pos.CENTER
        children.add(noResultsView)
        children.add(noSelectionView)
        children.addAll(panelMap.values)
        panelMap.values.forEach { it.isVisible = false; it.isManaged = false }

        categoryViewModel.searchQuery.addListener { _, _, _ ->
            val currentPanelId = categoryViewModel.selectedCategory.get()?.panelId
            showPanel(currentPanelId)
        }

        categoryViewModel.selectedCategory.addListener { _, _, newValue ->
            val panelId = newValue?.panelId
            showPanel(panelId)
        }

        showPanel(categoryViewModel.selectedCategory.get()?.panelId)
    }

    private fun showPanel(panelId: String?) {
        panelMap.values.forEach { node ->
            node.isVisible = false
            node.isManaged = false
        }
        if (panelId == null) {
            val showNoResults = categoryViewModel.searchQuery.get().isNotBlank()
            noResultsView.isVisible = showNoResults
            noResultsView.isManaged = showNoResults
            val showNoSelection = !showNoResults
            noSelectionView.isVisible = showNoSelection
            noSelectionView.isManaged = showNoSelection
            return
        }
        noResultsView.isVisible = false
        noResultsView.isManaged = false
        noSelectionView.isVisible = false
        noSelectionView.isManaged = false
        val panel = panelMap[panelId] ?: return
        panel.isVisible = true
        panel.isManaged = true
    }
}
