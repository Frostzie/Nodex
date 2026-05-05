package io.github.frostzie.nodex.services.workspace

import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import java.nio.file.Path

/**
 * Manages the active project lifecycle at runtime.
 */
class ProjectRuntimeService(
    private val fileWatcher: FileWatcher,
    private val fileTreeService: FileTree
) : ProjectRuntime {
    private val logger = LoggerProvider.getLogger("ProjectRuntimeService")
    private val _currentProject = SimpleObjectProperty<Project?>()
    override val currentProjectProperty: ReadOnlyObjectProperty<Project?> = _currentProject

    private val _loadedExpandedPaths = SimpleObjectProperty<Set<Path>>(emptySet())
    override val loadedExpandedPathsProperty: ReadOnlyObjectProperty<Set<Path>> = _loadedExpandedPaths

    private var isBuilding = false
    private var pendingTick = false

    private val tickListener = ChangeListener<Number> { _, _, _ ->
        val project = _currentProject.get()
        if (project != null) {
            if (isBuilding) {
                pendingTick = true
            } else {
                fileTreeService.onFilesystemTick(project)
            }
        }
    }

    /**
     * Switches the active project, rebuilding the file tree and watcher state.
     */
    override fun setProject(project: Project, loadedExpandedPaths: Set<Path>) {
        val oldProject = _currentProject.get()
        if (oldProject == project) return

        logger.debug("Setting current project to: {}", project.path)

        clearProject()

        _currentProject.set(project)
        _loadedExpandedPaths.set(loadedExpandedPaths)

        isBuilding = true
        pendingTick = false
        try {
            fileTreeService.build(project)
        } finally {
            isBuilding = false
        }

        fileWatcher.watch(project)

        project.filesystemTick.addListener(tickListener)

        if (pendingTick) {
            pendingTick = false
            fileTreeService.onFilesystemTick(project)
        }
    }

    /**
     * Clears the current project, stopping its watcher and flushing state.
     */
    override fun clearProject() {
        val project = _currentProject.get() ?: return

        project.filesystemTick.removeListener(tickListener)
        fileWatcher.unwatch(project.path)

        _currentProject.set(null)
        _loadedExpandedPaths.set(emptySet())
        pendingTick = false

        logger.debug("Project cleared: {}", project.path)
    }

    override fun getCurrentProject(): Project? = _currentProject.get()
}
