package io.github.frostzie.nodex.api.file

import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.domain.tree.FileTreeChange
import io.methvin.watcher.DirectoryChangeEvent.EventType
import java.nio.file.Path

/**
 * Observes file system changes for projects and individual files.
 * Provides raw event callbacks.
 *
 * @see io.github.frostzie.nodex.services.files.FileWatcherService
 */
interface FileWatcher {
    /**
     * Initializes the service by observing focus changes.
     */
    fun initialize()

    /**
     * Starts monitoring a specific file for changes.
     */
    fun watchFile(path: Path, onAction: (Path, EventType) -> Unit)

    /**
     * Stops monitoring a specific file.
     */
    fun unwatchFile(path: Path)

    /**
     * Starts monitoring disk state for a project.
     */
    fun watch(project: Project)

    /**
     * Stops monitoring a specific project root.
     */
    fun unwatch(root: Path)

    /**
     * Drains the current change queue for a given project root.
     */
    fun drainChanges(projectRoot: Path): List<FileTreeChange>

    /**
     * Temporarily ignores a path to prevent reload loops.
     */
    fun ignorePath(path: Path)

    /**
     * Stops monitoring all projects.
     */
    fun stopAll()

    /**
     * Permanently shuts down the file watcher and its background threads.
     */
    fun shutdown()
}
