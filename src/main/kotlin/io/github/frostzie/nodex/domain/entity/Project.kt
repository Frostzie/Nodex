package io.github.frostzie.nodex.domain.entity

import io.github.frostzie.nodex.domain.project.PackMcmeta
import javafx.beans.property.SimpleLongProperty
import java.nio.file.Path

/**
 * Represents a project root.
 */
data class Project(
    val path: Path,
    val name: String = path.fileName.toString(),
    var metadata: PackMcmeta? = null,
    var iconPath: Path? = null
) {
    /**
     * Observable state for filesystem sync.
     * Monotonic invalidation counter incremented when any disk change is detected.
     * A tick increment signals that any existing view of the project's data
     * (e.g., file tree, cached file content) may be stale.
     *
     * ViewModels should observe this tick and call [FileWatcherService.drainChanges]
     * to retrieve specific change details or rescan for UI updates.
     */
    val filesystemTick = SimpleLongProperty(0L)
}
