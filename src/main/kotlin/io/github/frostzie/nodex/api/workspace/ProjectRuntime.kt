package io.github.frostzie.nodex.api.workspace

import io.github.frostzie.nodex.domain.entity.Project
import javafx.beans.property.ReadOnlyObjectProperty
import java.nio.file.Path

/**
 * Manages the active project lifecycle at runtime.
 *
 * Tracks which project is currently loaded, manages file tree building,
 * filesystem watching, and saves/restores the expanded folder state
 * between project sessions.
 *
 * @see io.github.frostzie.nodex.services.workspace.ProjectRuntimeService
 */
interface ProjectRuntime {
    /** Read-only property containing the currently active project, or null. */
    val currentProjectProperty: ReadOnlyObjectProperty<Project?>

    /**
     * Read-only property of the file paths that were expanded in the file tree
     * during the last session for the currently loaded project.
     */
    val loadedExpandedPathsProperty: ReadOnlyObjectProperty<Set<Path>>

    /**
     * Switches the active project.
     *
     * Clears any previously loaded project, builds the file tree for [project],
     * starts filesystem watching, and restores the saved expanded state.
     * If the same project is already active, this is a no-op.
     *
     * @param project The project to load.
     */
    fun setProject(project: Project, loadedExpandedPaths: Set<Path>)

    /**
     * Clears the current project, stopping its watcher and flushing state.
     */
    fun clearProject()

    /**
     * Returns the currently active project, or `null` if no project is loaded.
     */
    fun getCurrentProject(): Project?
}
