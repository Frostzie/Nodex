package io.github.frostzie.nodex.services.config.global

import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.domain.entity.RecentProject
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Path
import java.time.Instant

/**
 * Recent projects and startup auto-open service.
 */
class RecentProjectsService(
    private val projectsConfigService: ProjectsConfigService
) : RecentProjects {
    override val recentProjects: ObservableList<RecentProject> = FXCollections.observableArrayList()

    private var initialized = false
    private var isDirty = false

    override fun initialize() {
        if (initialized) return
        initialized = true

        val loaded = projectsConfigService.load()
        val normalizedLoaded = normalizeProjects(loaded)
        recentProjects.setAll(normalizedLoaded)
        isDirty = normalizedLoaded != loaded

        projectsConfigService.initialize { reloaded ->
            val normalizedReloaded = normalizeProjects(reloaded)
            recentProjects.setAll(normalizedReloaded)
            isDirty = normalizedReloaded != reloaded
        }

    }

    override fun markProjectActive(path: Path) {
        val normalizedPath = normalizePath(path)
        val now = Instant.now()
        val existing = recentProjects.firstOrNull { normalizePath(it.path) == normalizedPath }

        val updated = recentProjects
            .filterNot { normalizePath(it.path) == normalizedPath }
            .map { it.copy(active = false) }
            .toMutableList()
            .apply {
                add(
                    0,
                    existing?.copy(path = normalizedPath, lastOpened = now, active = true)
                        ?: RecentProject(path = normalizedPath, lastOpened = now, active = true)
                )
            }

        recentProjects.setAll(updated)
        isDirty = true
    }

    override fun clearActiveProject() {
        val updated = recentProjects.map { if (it.active) it.copy(active = false) else it }
        if (updated != recentProjects.toList()) {
            recentProjects.setAll(updated)
            isDirty = true
        }
    }

    override fun getActiveProjectPath(): Path? =
        recentProjects
            .asSequence()
            .filter { it.active }
            .maxByOrNull { it.lastOpened }
            ?.path
            ?.let(::normalizePath)

    override fun removeProject(path: Path) {
        val normalizedPath = normalizePath(path)
        val updated = recentProjects.filterNot { normalizePath(it.path) == normalizedPath }
        if (updated.size != recentProjects.size) {
            recentProjects.setAll(updated)
            isDirty = true
        }
    }

    override fun flushPending() {
        if (!isDirty) return
        projectsConfigService.save(recentProjects.toList())
        isDirty = false
    }

    private fun normalizeProjects(projects: List<RecentProject>): List<RecentProject> {
        val normalized = projects
            .groupBy { normalizePath(it.path) }
            .map { (path, duplicates) ->
                val latestEntry = duplicates.maxByOrNull { it.lastOpened }
                RecentProject(
                    path = path,
                    lastOpened = latestEntry?.lastOpened ?: Instant.EPOCH,
                    active = duplicates.any { it.active }
                )
            }
            .sortedByDescending { it.lastOpened }

        val activeEntries = normalized.filter { it.active }
        if (activeEntries.size <= 1) return normalized

        val newestActivePath = activeEntries.maxByOrNull { it.lastOpened }?.path
        return normalized.map { project ->
            if (project.path == newestActivePath) project.copy(active = true) else project.copy(active = false)
        }
    }

    private fun normalizePath(path: Path): Path = path.toAbsolutePath().normalize()
}
