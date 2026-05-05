package io.github.frostzie.nodex.api.config

import io.github.frostzie.nodex.domain.entity.RecentProject
import javafx.collections.ObservableList
import java.nio.file.Path

/**
 * Handles recent projects state and lifecycle.
 *
 * @see io.github.frostzie.nodex.services.config.global.RecentProjectsService
 */
interface RecentProjects {
    /**
     * Current recent projects list, ordered by latest open first.
     */
    val recentProjects: ObservableList<RecentProject>

    fun initialize()

    fun markProjectActive(path: Path)

    fun clearActiveProject()

    /**
     * Returns the most recent active project path, or null if none exists.
     */
    fun getActiveProjectPath(): Path?

    /**
     * Removes a project entry from recents.
     */
    fun removeProject(path: Path)

    /**
     * Saves pending recents updates.
     */
    fun flushPending()
}
