package io.github.frostzie.nodex.api.file

import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.domain.tree.FileTreeChange
import io.github.frostzie.nodex.domain.tree.FileTreeState
import javafx.beans.property.ReadOnlyObjectProperty

/**
 * Manages in-memory state of the file tree in a project.
 *
 * Provides access to the current tree state and incremental
 * change notifications.
 *
 * @see io.github.frostzie.nodex.services.files.FileTreeService
 */
interface FileTree {

    /**
     * The current file tree state.
     */
    val stateProperty: ReadOnlyObjectProperty<FileTreeState>

    /**
     * The list of changes applied during the last filesystem tick.
     * Cleared on each tick.
     */
    val lastChangesProperty: ReadOnlyObjectProperty<List<FileTreeChange>>

    /**
     * Performs a full scan of [project] and replaces the current tree state.
     *
     * @param project The project whose root directory should be scanned.
     */
    fun build(project: Project)

    /**
     * Applies pending filesystem changes to the in-memory tree state.
     *
     * @param project The currently active project.
     */
    fun onFilesystemTick(project: Project)
}
