package io.github.frostzie.nodex.services.workspace

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle
import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import java.nio.file.Files
import java.nio.file.Path

/**
 * Default workspace lifecycle service.
 */
class WorkspaceLifecycleService(
    private val configService: Config,
    private val recentProjects: RecentProjects,
    private val projectRuntime: ProjectRuntime,
    private val editorSession: EditorSession,
    private val concurrency: Concurrency,
    private val fileTreePersistence: FileTreePersistence,
    private val layout: Layout,
) : WorkspaceLifecycle {

    override fun resolveStartupScreen(): AppScreen {
        if (!configService.introFinished) {
            return AppScreen.INTRO
        }

        val activePath = recentProjects.getActiveProjectPath() ?: return AppScreen.PROJECT_MANAGER
        return if (openProject(activePath)) AppScreen.IDE else AppScreen.PROJECT_MANAGER
    }

    override fun openProject(path: Path): Boolean {
        val normalizedPath = normalizePath(path)
        if (!isValidProjectPath(normalizedPath)) {
            recentProjects.removeProject(normalizedPath)
            recentProjects.flushPending()
            return false
        }

        val currentPath = projectRuntime.getCurrentProject()?.path?.let(::normalizePath)
        if (currentPath != null && currentPath != normalizedPath) {
            closeCurrentProject()
        }

        if (projectRuntime.getCurrentProject() == null) {
            val loadedPaths = fileTreePersistence.loadOnProjectOpen(normalizedPath)
            projectRuntime.setProject(Project(normalizedPath), loadedPaths)
        }
        recentProjects.markProjectActive(normalizedPath)
        layout.loadForProject(normalizedPath)
        return true
    }

    override fun closeCurrentProject() {
        editorSession.saveAll()
        editorSession.clear()
        recentProjects.flushPending()
        fileTreePersistence.flushPending()
        layout.saveForProject(projectRuntime.getCurrentProject()!!.path)
        projectRuntime.clearProject()
        recentProjects.clearActiveProject()
    }

    override fun shutdown() {
        concurrency.runLater {
            editorSession.saveAll()
            editorSession.clear()
        }
        recentProjects.flushPending()
        fileTreePersistence.flushPending()
        layout.saveForProject(projectRuntime.getCurrentProject()!!.path)
        projectRuntime.clearProject()
    }

    private fun normalizePath(path: Path): Path = path.toAbsolutePath().normalize()
    private fun isValidProjectPath(path: Path): Boolean = Files.exists(path) && Files.isDirectory(path)
}
