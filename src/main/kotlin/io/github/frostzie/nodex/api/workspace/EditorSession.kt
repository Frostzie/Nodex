package io.github.frostzie.nodex.api.workspace

import io.github.frostzie.nodex.domain.uicontract.EditorTab
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import java.nio.file.Path

/**
 * Owns editor document/tab state.
 *
 * @see io.github.frostzie.nodex.services.workspace.EditorSessionService
 */
interface EditorSession {
    /** Current list of open tabs in display order. */
    val tabs: ObservableList<EditorTab>

    /** Currently selected tab, or null if no tabs are open. */
    val selectedTab: ReadOnlyObjectProperty<EditorTab?>

    /** Opens the file in a tab and selects it. Reuses existing tab if already open. */
    fun openFile(path: Path): EditorTab

    /** Selects the tab by id. Returns false if not found. */
    fun selectTab(tabId: String): Boolean

    /** Updates content for a tab and marks it dirty. Returns false if no change. */
    fun updateContent(tabId: String, newContent: String): Boolean

    /** Saves a tab by id. Returns true only when a dirty tab was saved. */
    fun saveTab(tabId: String): Boolean

    /** Saves all dirty tabs. Returns the number of tabs saved. */
    fun saveAll(): Int

    /** Closes a tab by id. */
    fun closeTab(tabId: String, saveBeforeClose: Boolean = false): Boolean

    /** Finds a tab by id. */
    fun getTab(tabId: String): EditorTab?

    /** Clears all editor session state. */
    fun clear()
}
