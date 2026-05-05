package io.github.frostzie.nodex.api.config

import java.nio.file.Path

/**
 * Handles runtime persistence of the file tree state for projects.
 *
 * @see io.github.frostzie.nodex.services.files.FileTreePersistenceService
 */
interface FileTreePersistence {

    /**
     * Records a new expanded set and schedules a debounced save.
     *
     * @param projectRoot The root path of the project.
     * @param expanded The set of currently expanded directory paths.
     */
    fun onExpandedChanged(projectRoot: Path, expanded: Set<Path>)

    /**
     * Immediately writes any pending expanded state changes.
     */
    fun flushPending()

    /**
     * Loads the persisted expanded paths for a given [projectRoot].
     *
     * @param projectRoot The root path of the project.
     * @return The set of expanded directory paths from the last session.
     */
    fun loadOnProjectOpen(projectRoot: Path): Set<Path>
}