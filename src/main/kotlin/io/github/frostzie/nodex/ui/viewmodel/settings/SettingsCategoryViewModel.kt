package io.github.frostzie.nodex.ui.viewmodel.settings

import io.github.frostzie.nodex.ui.utils.settings.SettingsSearchEntry
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TreeItem

/**
 * ViewModel for the settings category tree.
 */
class SettingsCategoryViewModel(
    private val rootNode: SettingsCategoryNode,
    private val searchIndex: Map<String, List<SettingsSearchEntry>>
) {
    val searchQuery = SimpleStringProperty("")
    val rootProperty: ObjectProperty<TreeItem<SettingsCategoryNode>> = SimpleObjectProperty()
    val selectedCategory: ObjectProperty<SettingsCategoryNode?> = SimpleObjectProperty()

    init {
        rebuildTree()
        searchQuery.addListener { _, _, _ -> rebuildTree() }
    }

    private fun rebuildTree() {
        val query = searchQuery.get()
        val rootItem = buildTree(rootNode, query)
        rootProperty.set(rootItem)
        ensureSelection(rootItem)
    }

    private fun buildTree(node: SettingsCategoryNode, query: String): TreeItem<SettingsCategoryNode> {
        val item = TreeItem(node)
        if (node.children.isNotEmpty()) {
            val visibleChildren = node.children
                .mapNotNull { child ->
                    val childItem = buildTree(child, query)
                    if (shouldInclude(child, query, childItem)) childItem else null
                }
            item.children.setAll(visibleChildren)
        }
        return item
    }

    private fun shouldInclude(
        node: SettingsCategoryNode,
        query: String,
        builtItem: TreeItem<SettingsCategoryNode>
    ): Boolean {
        if (query.isBlank()) return true
        if (!node.searchable) return false
        if (node.children.isNotEmpty()) {
            return builtItem.children.isNotEmpty()
        }
        val entries = searchIndex[node.id].orEmpty()
        return entries.any { it.matches(query) }
    }

    private fun ensureSelection(rootItem: TreeItem<SettingsCategoryNode>) {
        val current = selectedCategory.get()
        if (current != null && current.panelId != null && containsNode(rootItem, current.id)) {
            return
        }

        val firstLeaf = findFirstLeaf(rootItem)
        selectedCategory.set(firstLeaf?.value)
    }

    private fun containsNode(item: TreeItem<SettingsCategoryNode>, id: String): Boolean {
        if (item.value.id == id) return true
        return item.children.any { containsNode(it, id) }
    }

    private fun findFirstLeaf(item: TreeItem<SettingsCategoryNode>): TreeItem<SettingsCategoryNode>? {
        if (item.children.isEmpty()) {
            return if (item.value.panelId != null) item else null
        }
        for (child in item.children) {
            val leaf = findFirstLeaf(child)
            if (leaf != null) return leaf
        }
        return null
    }
}
