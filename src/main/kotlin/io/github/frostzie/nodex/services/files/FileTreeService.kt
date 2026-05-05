package io.github.frostzie.nodex.services.files

import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.domain.tree.FileTreeChange
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.domain.tree.FileNodeState
import io.github.frostzie.nodex.domain.tree.FileTreeState
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

/**
 * Manages in-memory state of the file tree in a project.
 *
 * Maintains a snapshot of the project's file hierarchy as a
 * [FileTreeState] and applies incremental changes from the filesystem watcher
 * to keep it in sync without full rescan.
 *
 * @param fileWatcher Provides filesystem change events to react to.
 */
class FileTreeService(
    private val fileWatcher: FileWatcher
) : FileTree {
    private val logger = LoggerProvider.getLogger("FileTreeService")
    private val _state = SimpleObjectProperty<FileTreeState>()
    override val stateProperty: ReadOnlyObjectProperty<FileTreeState> = _state

    private val _lastChanges = SimpleObjectProperty<List<FileTreeChange>>(emptyList())
    override val lastChangesProperty: ReadOnlyObjectProperty<List<FileTreeChange>> = _lastChanges

    /**
     * Performs a full scan of the project root and replaces the current tree state.
     */
    override fun build(project: Project) {
        logger.debug("Building file tree for project: {}", project.path)
        val rootPath = project.path
        val state = buildState(rootPath)

        _lastChanges.set(listOf(FileTreeChange.FileSystemRescanRequired))
        _state.set(state)
    }

    private fun buildState(rootPath: Path): FileTreeState {
        val nodes = mutableMapOf<String, FileNodeState>()
        val rootId = rootPath.toAbsolutePath().toString()
        val rootIds = mutableListOf<String>()

        if (Files.exists(rootPath)) {
            rootIds.add(rootId)
            val childIds = mutableListOf<String>()
            scanDirectory(rootPath, nodes, childIds)

            nodes[rootId] = FileNodeState(
                id = rootId,
                path = rootPath,
                name = rootPath.name,
                isDirectory = true,
                childIds = childIds
            )
        }

        return FileTreeState(rootPath, nodes, rootIds)
    }

    private fun scanDirectory(
        directory: Path,
        nodes: MutableMap<String, FileNodeState>,
        childIds: MutableList<String>
    ) {
        try {
            directory.listDirectoryEntries().forEach { path ->
                if (shouldIgnore(path)) return@forEach

                val id = path.toAbsolutePath().toString()
                val isDirectory = Files.isDirectory(path)
                val nodeChildIds = mutableListOf<String>()

                if (isDirectory) {
                    scanDirectory(path, nodes, nodeChildIds)
                }

                nodes[id] = FileNodeState(
                    id = id,
                    path = path,
                    name = path.name,
                    isDirectory = isDirectory,
                    childIds = nodeChildIds
                )
                childIds.add(id)
            }
        } catch (e: Exception) {
            logger.error("Failed to scan directory: $directory", e)
        }
    }

    private fun shouldIgnore(path: Path): Boolean {
        return path.isSymbolicLink()
    }

    /**
     * Applies pending filesystem changes to the in-memory tree state.
     */
    override fun onFilesystemTick(project: Project) {
        val changes = fileWatcher.drainChanges(project.path)
        if (changes.isEmpty()) return

        if (changes.any { it is FileTreeChange.FileSystemRescanRequired }) {
            build(project)
            return
        }

        val currentState = _state.get() ?: return
        var updatedState = currentState
        var stateChanged = false

        for (change in changes) {
            when (change) {
                is FileTreeChange.FileCreated -> {
                    updatedState = handleFileCreated(updatedState, change.path)
                    stateChanged = true
                }

                is FileTreeChange.FileDeleted -> {
                    updatedState = handleFileDeleted(updatedState, change.path)
                    stateChanged = true
                }

                is FileTreeChange.ParentInvalidated -> {
                    updatedState = handleParentInvalidated(updatedState)
                    stateChanged = true
                }

                is FileTreeChange.FileModified -> {
                    // Ignore for now as it doesn't affect tree yet
                }

                FileTreeChange.FileSystemRescanRequired -> {
                    build(project)
                    return
                }
            }
        }

        if (stateChanged) {
            _lastChanges.set(changes)
            _state.set(updatedState)
        }
    }

    private fun handleFileCreated(state: FileTreeState, path: Path): FileTreeState {
        if (shouldIgnore(path)) return state

        val id = path.toAbsolutePath().toString()
        if (state.nodes.containsKey(id)) return state

        if (Files.isDirectory(path)) {
            // Since also dir renames could trigger for now the easiest is just rescanning the whole tree.
            //TODO: Eventually improved...
            return handleParentInvalidated(state)
        }

        val childIds = emptyList<String>()

        val newNode = FileNodeState(
            id = id,
            path = path,
            name = path.name,
            isDirectory = false,
            childIds = childIds
        )

        val newNodes = state.nodes.toMutableMap()
        newNodes[id] = newNode

        val parentPath = path.parent
        val parentId = parentPath?.toAbsolutePath()?.toString() ?: state.rootIds.firstOrNull()

        if (parentId != null) {
            val parentNode = newNodes[parentId]
            if (parentNode != null) {
                val newParentChildIds = parentNode.childIds.toMutableList()
                if (!newParentChildIds.contains(id)) {
                    newParentChildIds.add(id)
                    newNodes[parentId] = parentNode.copy(childIds = newParentChildIds)
                }
            }
        }

        return state.copy(nodes = newNodes)
    }

    private fun handleFileDeleted(state: FileTreeState, path: Path): FileTreeState {
        val id = path.toAbsolutePath().toString()
        if (!state.nodes.containsKey(id)) return state

        val newNodes = state.nodes.toMutableMap()
        removeNodeRecursive(id, newNodes)

        val parentPath = path.parent
        val parentId = parentPath?.toAbsolutePath()?.toString() ?: state.rootIds.firstOrNull()

        if (parentId != null) {
            val parentNode = newNodes[parentId]
            if (parentNode != null) {
                val newParentChildIds = parentNode.childIds.toMutableList()
                if (newParentChildIds.remove(id)) {
                    newNodes[parentId] = parentNode.copy(childIds = newParentChildIds)
                }
            }
        }

        return state.copy(nodes = newNodes)
    }

    private fun removeNodeRecursive(id: String, nodes: MutableMap<String, FileNodeState>) {
        val node = nodes.remove(id) ?: return
        node.childIds.forEach { childId ->
            removeNodeRecursive(childId, nodes)
        }
    }

    private fun handleParentInvalidated(state: FileTreeState): FileTreeState {
        return buildState(state.rootPath)
    }
}
