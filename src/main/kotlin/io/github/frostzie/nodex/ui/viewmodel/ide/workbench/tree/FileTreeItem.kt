package io.github.frostzie.nodex.ui.viewmodel.ide.workbench.tree

import java.nio.file.Path

/**
 * Single item in the file tree.
 */
data class FileTreeItem(
    val path: Path,
    val displayName: String,
    val isDirectory: Boolean,
    val isDummy: Boolean = false
) {
    /**
     * Overriding toString() provides a default, readable name
     * that the TreeView can display.
     */
    override fun toString(): String {
        return displayName
    }
}
