package io.github.frostzie.nodex.services.files

import io.github.frostzie.nodex.domain.entity.Project
import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.domain.tree.FileTreeChange
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.utils.LoggerProvider
import io.github.frostzie.nodex.utils.file.FileSystemWatcher
import io.methvin.watcher.DirectoryChangeEvent.EventType
import java.nio.file.Path
import java.util.concurrent.*

/**
 * Observes file system changes for projects and individual files.
 *
 * This service interprets raw disk changes into state updates.
 * It makes sure the external changes sync with the mod.
 */
class FileWatcherService(
    private val focusService: FocusTracker,
    private val concurrency: Concurrency
) : FileWatcher {
    private val logger = LoggerProvider.getLogger("FileWatcherService")
    private val watchers = ConcurrentHashMap<Path, ProjectWatcher>()
    private val genericWatchers = ConcurrentHashMap<Path, FileSystemWatcher>()
    private val fileCallbacks = ConcurrentHashMap<Path, ConcurrentHashMap<Path, (Path, EventType) -> Unit>>()

    private val syncExecutor = Executors.newSingleThreadScheduledExecutor()
    private val ignoredPaths = ConcurrentHashMap<Path, Long>()
    private val ignoreDurationMs = 2000L
    private val projectChangeQueues = ConcurrentHashMap<Path, ConcurrentLinkedQueue<FileTreeChange>>()
    private val debounceDelayMs = 500L
    private val maxQueueSize = 50 // Possibly allow changing this through settings

    private val pendingProjectsToSync = ConcurrentHashMap.newKeySet<Path>()
    private val pendingSyncs = ConcurrentHashMap<Path, ScheduledFuture<*>>()

    private data class ProjectWatcher(
        val project: Project,
        val watcher: FileSystemWatcher
    )

    /**
     * Initializes the service by observing focus changes.
     */
    override fun initialize() {
        focusService.isFocused.addListener { _, _, focused ->
            if (focused) {
                drainPendingSyncs()
            }
        }
    }

    /**
     * Starts monitoring a specific file for changes.
     * 
     * Since DirectoryWatcher monitors directories, this watches the parent
     * directory and filters events for the target file.
     * 
     * @param path The file to watch.
     * @param onAction The callback to invoke when the file changes.
     */
    override fun watchFile(path: Path, onAction: (Path, EventType) -> Unit) {
        val normalizedPath = path.toAbsolutePath().normalize()
        val parent = normalizedPath.parent ?: return
        val fileName = normalizedPath.fileName

        val callbacks = fileCallbacks.computeIfAbsent(parent) { ConcurrentHashMap() }
        callbacks[fileName] = onAction

        genericWatchers.computeIfAbsent(parent) {
            val watcher = FileSystemWatcher(parent) { eventPath, action ->
                if (!shouldIgnore(eventPath)) {
                    fileCallbacks[parent]?.get(eventPath.fileName)?.invoke(eventPath, action)
                }
            }
            watcher.start()
            logger.debug("Monitoring parent directory for file: {}", normalizedPath)
            watcher
        }
    }

    override fun unwatchFile(path: Path) {
        val normalizedPath = path.toAbsolutePath().normalize()
        val parent = normalizedPath.parent ?: return
        val fileName = normalizedPath.fileName

        val callbacks = fileCallbacks[parent] ?: return
        callbacks.remove(fileName)

        if (callbacks.isEmpty()) {
            fileCallbacks.remove(parent)
            genericWatchers.remove(parent)?.stop()
        }
    }

    /**
     * Starts monitoring disk state for a project.
     *
     * Triggers project-level invalidation (via filesystemTick) upon disk changes.
     * It provides file-level change details through [drainChanges], signaling that
     * parts of the project need to be re-synced.
     *
     * The [Project.filesystemTick] acts as the primary trigger; listeners should call
     * [drainChanges] to retrieve the actual modification details.
     */
    override fun watch(project: Project) {
        val root = project.path
        watchers.computeIfAbsent(root) {
            val watcher = FileSystemWatcher(root) { path, action ->
                onRawAction(project, path, action)
            }
            watcher.start()
            logger.debug("Monitoring project state at: {}", root)
            ProjectWatcher(project, watcher)
        }
    }

    /**
     * Stops monitoring a specific project root.
     * 
     * @param root The root path of the project to stop watching.
     */
    override fun unwatch(root: Path) {
        watchers.remove(root)?.watcher?.stop()
        pendingSyncs.remove(root)?.cancel(false)
        pendingProjectsToSync.remove(root)
        projectChangeQueues.remove(root)
        logger.debug("Stopped monitoring project at: {}", root)
    }

    /**
     * Drains the current change queue for a given project root.
     *
     * Clears the queue and ensures events are returned in the exact order they were sent.
     *
     * @param projectRoot The root path of the project.
     * @return A list of [FileTreeChange] events that have occurred since the last drain.
     */
    override fun drainChanges(projectRoot: Path): List<FileTreeChange> {
        val queue = projectChangeQueues[projectRoot] ?: return emptyList()
        val changes = mutableListOf<FileTreeChange>()
        while (true) {
            val change = queue.poll() ?: break
            changes.add(change)
        }
        return changes
    }

    /**
     * Temporarily ignores a path to prevent reload loops.
     */
    override fun ignorePath(path: Path) {
        ignoredPaths[path] = System.currentTimeMillis()
    }

    /**
     * Entrypoint for raw file system events, intended for internal use and testing.
     * Interprets raw [EventType] actions into high-level [FileTreeChange] events.
     */
    internal fun onRawAction(project: Project, path: Path, action: EventType) {
        if (shouldIgnore(path)) {
            logger.debug("Ignoring internal change at: {}", path)
            return
        }

        val root = project.path

        // Always track specific events to avoid forced full rescans
        handleRawEvent(project, path, action)

        if (!focusService.isFocusedSnapshot) {
            pendingProjectsToSync.add(root)
        }
    }

    private fun handleRawEvent(project: Project, path: Path, action: EventType) {
        val root = project.path
        when (action) {
            EventType.MODIFY -> {
                enqueueChange(root, FileTreeChange.FileModified(path))
                scheduleSync(project)
            }

            EventType.DELETE -> {
                enqueueChange(root, FileTreeChange.FileDeleted(path))
                scheduleSync(project)
            }

            EventType.CREATE -> {
                enqueueChange(root, FileTreeChange.FileCreated(path))
                scheduleSync(project)
            }

            else -> {}
        }
    }

    /**
     * Enqueues a change for a project, with measures to prevent UI flooding.
     *
     * If too many changes accumulate before a sync, the queue is cleared and a full
     * project rescan is triggered. This prevents processing hundreds of specific events
     * and ensures the tree state is consistent.
     */ // First time using a word like "enqueue" but sure google ig it describes it the best lol
    private fun enqueueChange(root: Path, change: FileTreeChange) {
        val queue = projectChangeQueues.computeIfAbsent(root) { ConcurrentLinkedQueue() }

        val first = queue.peek()
        if (first is FileTreeChange.FileSystemRescanRequired) return

        if (queue.size >= maxQueueSize || change is FileTreeChange.FileSystemRescanRequired) {
            queue.clear()
            queue.add(FileTreeChange.FileSystemRescanRequired)
            return
        }

        queue.add(change)
    }

    private fun shouldIgnore(path: Path): Boolean {
        val now = System.currentTimeMillis()
        val iterator = ignoredPaths.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > ignoreDurationMs) {
                iterator.remove()
                continue
            }
            if (path.startsWith(entry.key)) return true
        }
        return false
    }

    private fun scheduleSync(project: Project) {
        val root = project.path
        pendingSyncs[root]?.cancel(false)
        pendingSyncs[root] = syncExecutor.schedule({
            pendingSyncs.remove(root)
            if (focusService.isFocusedSnapshot) {
                notifySync(project)
            } else {
                pendingProjectsToSync.add(root)
            }
        }, debounceDelayMs, TimeUnit.MILLISECONDS)
    }

    private fun drainPendingSyncs() {
        val iterator = pendingProjectsToSync.iterator()
        while (iterator.hasNext()) {
            val root = iterator.next()
            iterator.remove()
            val watcher = watchers[root]
            if (watcher != null) {
                notifySync(watcher.project)
            }
        }
    }

    private fun notifySync(project: Project) {
        // Increment state tick on UI thread to notify observers
        concurrency.runOnUI {
            project.filesystemTick.set(project.filesystemTick.get() + 1)
        }
    }

    /**
     * Stops monitoring all projects.
     *
     * Resets the watcher state but does not shut down the background executor.
     */
    override fun stopAll() {
        watchers.values.forEach { it.watcher.stop() }
        watchers.clear()

        genericWatchers.values.forEach { it.stop() }
        genericWatchers.clear()
        fileCallbacks.clear()

        pendingSyncs.values.forEach { it.cancel(false) }
        pendingSyncs.clear()

        pendingProjectsToSync.clear()
        ignoredPaths.clear()
        projectChangeQueues.clear()

        logger.debug("Stopped all monitoring and cleared pending state")
    }

    /**
     * Permanently shuts down the file watcher service and its background threads.
     */
    override fun shutdown() {
        stopAll()
        syncExecutor.shutdownNow()
    }

    internal fun getWatcherCount(): Int = watchers.size // Only used by test
}
