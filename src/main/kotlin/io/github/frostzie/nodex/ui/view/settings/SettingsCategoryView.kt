package io.github.frostzie.nodex.ui.view.settings

import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryNode
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryViewModel
import javafx.application.Platform
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

/**
 * Sidebar for settings categories.
 */
class SettingsCategoryView(
    private val vm: SettingsCategoryViewModel
) : VBox(8.0) {
    private val treeView = TreeView<SettingsCategoryNode>()

    init {
        treeView.rootProperty().bind(vm.rootProperty)
        treeView.isShowRoot = false
        treeView.setCellFactory {
            object : TreeCell<SettingsCategoryNode>() {
                override fun updateItem(item: SettingsCategoryNode?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null else item.label
                }
            }
        }

        treeView.selectionModel.selectedItemProperty().addListener { _, _, newItem ->
            vm.selectedCategory.set(newItem?.value)
        }

        vm.selectedCategory.addListener { _, _, newValue ->
            if (newValue != null) {
                selectById(treeView.root, newValue.id)
            }
        }

        children.add(treeView)
        setVgrow(treeView, Priority.ALWAYS)
        Platform.runLater { treeView.requestFocus() }
    }

    private fun selectById(root: TreeItem<SettingsCategoryNode>?, id: String): Boolean {
        if (root == null) return false
        if (root.value.id == id) {
            treeView.selectionModel.select(root)
            return true
        }
        for (child in root.children) {
            if (selectById(child, id)) return true
        }
        return false
    }
}
