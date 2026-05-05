package io.github.frostzie.nodex.services.files

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.domain.config.TreeConfig
import io.github.frostzie.nodex.services.config.project.TreeConfigService
import io.github.frostzie.nodex.utils.LoggerProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.milliseconds

/**
 *
 * Manages runtime persistence of the file tree state.
 *
 * @param concurrency Provides the coroutine scope for debounced saves.
 * @param treeConfigService Loads and saves the tree config JSON per project.
 */
class FileTreePersistenceService(
    private val concurrency: Concurrency,
    private val treeConfigService: TreeConfigService
) : FileTreePersistence {
    private val logger = LoggerProvider.getLogger("FileTreePersistenceService")
    private var saveJob: Job? = null
    private val saveMutex = Mutex()

    private var cachedProjectRoot: Path? = null
    private var cachedExpanded: Set<Path>? = null

    /**
     * Records a new expanded set and schedules a debounced save.
     */
    override fun onExpandedChanged(projectRoot: Path, expanded: Set<Path>) {
        if (cachedProjectRoot == projectRoot && cachedExpanded == expanded) return

        cachedProjectRoot = projectRoot
        cachedExpanded = expanded
        scheduleSave(projectRoot, expanded)
    }

    private fun scheduleSave(projectRoot: Path, expanded: Set<Path>) {
        saveJob?.cancel()
        saveJob = concurrency.ioScope.launch {
            delay(500.milliseconds)
            saveMutex.withLock {
                saveNow(projectRoot, expanded)
            }
            clearCache(projectRoot)
        }
    }

    /**
     * Immediately writes any pending expanded state changes.
     */
    override fun flushPending() {
        saveJob?.cancel()
        saveJob = null
        val root = cachedProjectRoot
        val expanded = cachedExpanded
        if (root != null && expanded != null) {
            runBlocking {
                saveMutex.withLock {
                    saveNow(root, expanded)
                }
            }
            clearCache(root)
        }
    }

    private fun clearCache(projectRoot: Path) {
        if (cachedProjectRoot == projectRoot) {
            cachedProjectRoot = null
            cachedExpanded = null
        }
    }

    private fun saveNow(projectRoot: Path, expanded: Set<Path>) {
        val expandedStrings = expanded
            .mapNotNull { treeConfigService.toStoragePath(projectRoot, it) }
            .distinct()
            .sorted()
            .toMutableList()
        val config = TreeConfig(expandedStrings)
        treeConfigService.save(projectRoot, config)
    }

    /**
     * Loads the persisted expanded paths for a given project.
     */
    override fun loadOnProjectOpen(projectRoot: Path): Set<Path> {
        val config = treeConfigService.load(projectRoot)
        val paths = mutableSetOf<Path>()
        config.expanded.forEach { stored ->
            val path = treeConfigService.fromStoragePath(projectRoot, stored)
            if (Files.exists(path)) {
                paths.add(path)
            } else {
                logger.warn("Ignoring missing path in tree config: $stored")
            }
        }
        return paths
    }
}
