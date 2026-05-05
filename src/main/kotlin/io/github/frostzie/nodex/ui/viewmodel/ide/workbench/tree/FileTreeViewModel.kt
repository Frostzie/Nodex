package io.github.frostzie.nodex.ui.viewmodel.ide.workbench.tree

import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.domain.tree.FileTreeState
import io.github.frostzie.nodex.domain.tree.FileTreeChange
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeItem
import java.nio.file.Path

/**
 * ViewModel for the file tree, responsible for managing the tree structure.
 */
class FileTreeViewModel(
    private val fileTreeService: FileTree,
    projectRuntimeService: ProjectRuntime,
    private val fileTreePersistenceService: FileTreePersistence
) {
    val root = SimpleObjectProperty<TreeItem<FileTreeItem>>()
    val nodeCache = mutableMapOf<String, TreeItem<FileTreeItem>>()
    val invalidatedDirs = mutableSetOf<Path>()
    val selectedPath = SimpleObjectProperty<Path?>()

    private var initialExpanded: Set<Path>? = null
    private var lastNotifiedExpanded: Set<Path>? = null

    // TODO: Replace this with natural sorting utility
    private val nameComparator = compareBy<TreeItem<FileTreeItem>> { !it.value.isDirectory }
        .thenBy(String.CASE_INSENSITIVE_ORDER) { it.value.displayName }

    init {
        initialExpanded = projectRuntimeService.loadedExpandedPathsProperty.get()
        lastNotifiedExpanded = initialExpanded
        projectRuntimeService.loadedExpandedPathsProperty.addListener { _, _, newExpanded ->
            if (newExpanded.isNotEmpty()) {
                initialExpanded = newExpanded
            }
        }

        fileTreeService.stateProperty.addListener { _, _, newState ->
            if (newState != null) {
                updateTree(newState)
            }
        }

        fileTreeService.lastChangesProperty.addListener { _, _, changes ->
            changes?.forEach { change ->
                if (change is FileTreeChange.ParentInvalidated) {
                    handleParentInvalidated(change.parentPath)
                }
            }
        }

        // without this the tree won't be displayed
        fileTreeService.stateProperty.get()?.let { updateTree(it) }
    }

    private fun handleParentInvalidated(parentPath: Path) {
        var current: Path? = parentPath
        while (current != null) {
            val item = nodeCache[current.toAbsolutePath().toString()]
            if (item != null) {
                item.isExpanded = false
                invalidatedDirs.add(current)

                // Clear selection if it's under this invalidated ancestor
                val lastSelectedPath = selectedPath.get()
                if (lastSelectedPath != null && isUnderInvalidatedOrCollapsedAncestor(lastSelectedPath)) {
                    selectedPath.set(null)
                }
                break
            }
            current = current.parent
        }
    }

    /**
     * Syncs the view tree against the latest FileTreeState, preserving selection and expansion.
     */
    private fun updateTree(state: FileTreeState) {
        val rootPath = state.rootPath
        val rootId = state.rootIds.firstOrNull() ?: return
        val lastSelectedPath = selectedPath.get()

        val currentRoot = root.get()
        if (currentRoot == null || currentRoot.value.path != rootPath) {
            nodeCache.clear()
            invalidatedDirs.clear()
            val newRoot = createTreeItem(rootId, state, initialExpanded)
            newRoot.isExpanded = true
            root.set(newRoot)
            initialExpanded = null // Apply only once
        } else {
            updateNodeRecursive(currentRoot, state)
        }

        // Selection preservation
        if (lastSelectedPath != null) {
            val id = lastSelectedPath.toAbsolutePath().toString()
            if (nodeCache.containsKey(id) && !isUnderInvalidatedOrCollapsedAncestor(lastSelectedPath)) {
                selectedPath.set(lastSelectedPath)
            } else {
                selectedPath.set(null)
            }
        }

        notifyExpandedChanged()
    }

    /**
     * Builds a TreeItem subtree and wires expansion handling for invalidated folders.
     */
    private fun createTreeItem(
        id: String,
        state: FileTreeState,
        initialExpanded: Set<Path>? = null
    ): TreeItem<FileTreeItem> {
        val nodeState = state.nodes[id] ?: throw IllegalArgumentException("Node $id not found in state")
        val item = TreeItem(FileTreeItem(nodeState.path, nodeState.name, nodeState.isDirectory))
        nodeCache[id] = item

        if (nodeState.isDirectory) {
            if (initialExpanded?.contains(nodeState.path) == true) {
                item.isExpanded = true
            }

            item.expandedProperty().addListener { _, _, expanded ->
                if (expanded) {
                    if (invalidatedDirs.contains(item.value.path) || hasDummy(item)) {
                        refreshNode(item)
                        invalidatedDirs.remove(item.value.path)
                    }
                }
                notifyExpandedChanged()
            }

            if (item.isExpanded || id == state.rootPath.toAbsolutePath().toString()) {
                nodeState.childIds.forEach { childId ->
                    item.children.add(createTreeItem(childId, state, initialExpanded))
                }
                item.children.sortWith(nameComparator)
            } else if (nodeState.childIds.isNotEmpty()) {
                addDummy(item)
            }
        }
        return item
    }

    private fun updateNodeRecursive(item: TreeItem<FileTreeItem>, state: FileTreeState) {
        val id = item.value.path.toAbsolutePath().toString()
        val nodeState = state.nodes[id] ?: return

        if (!nodeState.isDirectory) return

        if (invalidatedDirs.contains(item.value.path)) return

        if (!item.isExpanded && item != root.get()) {
            val currentChildIds = item.children
                .filter { !it.value.isDummy }
                .map { it.value.path.toAbsolutePath().toString() }.toSet()
            val newChildIds = nodeState.childIds.toSet()
            if (currentChildIds != newChildIds) {
                invalidatedDirs.add(item.value.path)
            }

            if (nodeState.childIds.isNotEmpty() && item.children.isEmpty()) {
                addDummy(item)
            } else if (nodeState.childIds.isEmpty() && hasDummy(item)) {
                item.children.clear()
            }
            return
        }

        // Sync children
        val currentChildIds = item.children
            .filter { !it.value.isDummy }
            .map { it.value.path.toAbsolutePath().toString() }.toSet()
        val newChildIds = nodeState.childIds.toSet()

        // Remove
        item.children.removeIf { child ->
            if (child.value.isDummy) return@removeIf true
            val childId = child.value.path.toAbsolutePath().toString()
            if (childId !in newChildIds) {
                removeFromCacheRecursive(child)
                true
            } else false
        }

        // Add/Update
        nodeState.childIds.forEach { childId ->
            if (childId !in currentChildIds) {
                item.children.add(createTreeItem(childId, state))
            } else {
                val childItem = item.children.find { it.value.path.toAbsolutePath().toString() == childId }
                if (childItem != null) {
                    updateNodeRecursive(childItem, state)
                }
            }
        }
        item.children.sortWith(nameComparator)
    }

    private fun refreshNode(item: TreeItem<FileTreeItem>) {
        val id = item.value.path.toAbsolutePath().toString()
        val state = fileTreeService.stateProperty.get() ?: return
        val nodeState = state.nodes[id] ?: return

        item.children.clear()
        nodeState.childIds.forEach { childId ->
            item.children.add(createTreeItem(childId, state))
        }
        item.children.sortWith(nameComparator)
    }

    private fun addDummy(item: TreeItem<FileTreeItem>) {
        item.children.add(TreeItem(FileTreeItem(item.value.path, "dummy", false, isDummy = true)))
    }

    private fun hasDummy(item: TreeItem<FileTreeItem>): Boolean {
        return item.children.any { it.value.isDummy }
    }

    private fun removeFromCacheRecursive(item: TreeItem<FileTreeItem>) {
        if (!item.value.isDummy) {
            nodeCache.remove(item.value.path.toAbsolutePath().toString())
        }
        item.children.forEach { removeFromCacheRecursive(it) }
    }

    private fun isUnderInvalidatedOrCollapsedAncestor(path: Path): Boolean {
        var current: Path? = path.parent
        val rootItem = root.get() ?: return false
        val rootPath = rootItem.value.path

        while (current != null) {
            if (invalidatedDirs.contains(current)) return true
            val item = nodeCache[current.toAbsolutePath().toString()]
            if (item != null && !item.isExpanded && item != rootItem) return true
            if (current == rootPath) break
            current = current.parent
        }
        return false
    }

    private fun notifyExpandedChanged() {
        val rootPath = fileTreeService.stateProperty.get()?.rootPath ?: return
        val expanded = nodeCache.values
            .filter { it.isExpanded && it != root.get() }
            .map { it.value.path }
            .toSet()

        if (expanded != lastNotifiedExpanded) {
            lastNotifiedExpanded = expanded
            fileTreePersistenceService.onExpandedChanged(rootPath, expanded)
        }
    }
}
