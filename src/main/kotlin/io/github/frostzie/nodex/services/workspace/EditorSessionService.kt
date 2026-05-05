package io.github.frostzie.nodex.services.workspace

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.domain.uicontract.EditorTab
import io.methvin.watcher.DirectoryChangeEvent.EventType
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Default editor session implementation.
 */
class EditorSessionService(
    private val fileOperations: FileOperations,
    private val fileWatcher: FileWatcher,
    private val concurrency: Concurrency,
    private val autosaveDelayMs: Long = 900L,
    //TODO: Move to Concurrency
    private val autosaveScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "EditorSessionAutosave").apply { isDaemon = true }
    }
) : EditorSession {
    private val _tabs = FXCollections.observableArrayList<EditorTab>()
    override val tabs: ObservableList<EditorTab> = _tabs

    private val _selectedTab = SimpleObjectProperty<EditorTab?>(null)
    override val selectedTab: ReadOnlyObjectProperty<EditorTab?> = _selectedTab
    private val pendingAutosaves = ConcurrentHashMap<String, ScheduledFuture<*>>()
    private val watchedPaths = ConcurrentHashMap.newKeySet<Path>()

    override fun openFile(path: Path): EditorTab {
        val normalizedPath = normalizePath(path)
        val existing = _tabs.find { normalizePath(it.path) == normalizedPath }
        if (existing != null) {
            selectTab(existing.id)
            return requireNotNull(getTab(existing.id))
        }

        val content = fileOperations.readText(normalizedPath)
        val newTab = EditorTab(
            id = normalizedPath.toUri().toString(),
            path = normalizedPath,
            fileName = normalizedPath.fileName?.toString() ?: "Untitled",
            content = content,
            dirty = false,
            isActive = false
        )

        val selected = _selectedTab.get()
        val insertIndex = if (selected == null) _tabs.size else {
            (_tabs.indexOfFirst { it.id == selected.id } + 1).coerceAtLeast(0)
        }

        _tabs.add(insertIndex, newTab)
        ensurePathWatch(normalizedPath)
        selectTab(newTab.id)
        return requireNotNull(getTab(newTab.id))
    }

    override fun selectTab(tabId: String): Boolean {
        val selected = _tabs.find { it.id == tabId } ?: return false
        _selectedTab.set(selected)
        return true
    }

    override fun updateContent(tabId: String, newContent: String): Boolean {
        val index = _tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return false

        val current = _tabs[index]
        if (current.content == newContent) return false

        val updated = current.copy(content = newContent, dirty = true)
        _tabs[index] = updated
        if (_selectedTab.get()?.id == tabId) {
            _selectedTab.set(updated)
        }
        scheduleAutosave(tabId)
        return true
    }

    override fun saveTab(tabId: String): Boolean {
        cancelAutosave(tabId)

        val index = _tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return false

        val current = _tabs[index]
        if (!current.dirty) return false

        fileOperations.writeAtomic(current.path, current.content)
        val saved = current.copy(dirty = false)
        _tabs[index] = saved
        if (_selectedTab.get()?.id == tabId) {
            _selectedTab.set(saved)
        }
        return true
    }

    override fun saveAll(): Int {
        val dirtyIds = _tabs.filter { it.dirty }.map { it.id }
        var savedCount = 0
        dirtyIds.forEach { id ->
            if (saveTab(id)) {
                savedCount++
            }
        }
        return savedCount
    }

    override fun closeTab(tabId: String, saveBeforeClose: Boolean): Boolean {
        cancelAutosave(tabId)

        val index = _tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return false

        val tab = _tabs[index]
        if (tab.dirty && !saveBeforeClose) {
            return false
        }
        if (tab.dirty && !saveTab(tabId)) {
            return false
        }

        val selectedIdBeforeClose = _selectedTab.get()?.id
        releasePathWatch(normalizePath(tab.path))
        _tabs.removeAt(index)

        if (_tabs.isEmpty()) {
            _selectedTab.set(null)
            return true
        }

        if (selectedIdBeforeClose == tabId) {
            val fallbackIndex = index.coerceAtMost(_tabs.lastIndex)
            selectTab(_tabs[fallbackIndex].id)
        } else {
            selectedIdBeforeClose?.let { selectTab(it) }
        }

        return true
    }

    override fun getTab(tabId: String): EditorTab? = _tabs.find { it.id == tabId }

    override fun clear() {
        pendingAutosaves.keys.forEach { cancelAutosave(it) }
        _tabs.map { normalizePath(it.path) }.forEach(::releasePathWatch)
        _tabs.clear()
        _selectedTab.set(null)
    }

    private fun scheduleAutosave(tabId: String) {
        cancelAutosave(tabId)
        val task = autosaveScheduler.schedule({
            concurrency.runOnUI {
                pendingAutosaves.remove(tabId)
                saveTab(tabId)
            }
        }, autosaveDelayMs, TimeUnit.MILLISECONDS)
        pendingAutosaves[tabId] = task
    }

    private fun cancelAutosave(tabId: String) {
        pendingAutosaves.remove(tabId)?.cancel(false)
    }

    private fun ensurePathWatch(path: Path) {
        if (!watchedPaths.add(path)) {
            return
        }

        fileWatcher.watchFile(path) { changedPath, action ->
            if (action != EventType.MODIFY && action != EventType.CREATE) {
                return@watchFile
            }

            val normalizedPath = normalizePath(changedPath)
            concurrency.runOnUI {
                reloadFromDisk(normalizedPath)
            }
        }
    }

    private fun releasePathWatch(path: Path) {
        if (watchedPaths.remove(path)) {
            fileWatcher.unwatchFile(path)
        }
    }

    private fun reloadFromDisk(path: Path) {
        val index = _tabs.indexOfFirst { normalizePath(it.path) == path }
        if (index < 0) return

        val tab = _tabs[index]
        val diskContent = try {
            fileOperations.readText(tab.path)
        } catch (_: Exception) {
            return
        }

        if (tab.content == diskContent && !tab.dirty) {
            return
        }

        cancelAutosave(tab.id)
        val reloadedTab = tab.copy(content = diskContent, dirty = false)
        _tabs[index] = reloadedTab
        if (_selectedTab.get()?.id == tab.id) {
            _selectedTab.set(reloadedTab)
        }
    }

    private fun normalizePath(path: Path): Path = path.toAbsolutePath().normalize()
}
