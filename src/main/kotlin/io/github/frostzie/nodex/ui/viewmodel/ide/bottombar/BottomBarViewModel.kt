package io.github.frostzie.nodex.ui.viewmodel.ide.bottombar

import io.github.frostzie.nodex.domain.entity.ModInfo
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty

class BottomBarViewModel(
    private val monitor: PerformanceMonitor
) {
    val ideVersionProperty: ReadOnlyStringProperty = SimpleStringProperty("Nodex v${ModInfo.version}")

    val fpsProperty: ReadOnlyStringProperty get() = monitor.fpsProperty
    val memoryProperty: ReadOnlyStringProperty get() = monitor.memoryProperty

    private val _isDocumentLocked = ReadOnlyBooleanWrapper(false)
    val isDocumentLocked: ReadOnlyBooleanProperty = _isDocumentLocked.readOnlyProperty

    private val _isDocumentPresent = ReadOnlyBooleanWrapper(false)
    val isDocumentPresent: ReadOnlyBooleanProperty = _isDocumentPresent.readOnlyProperty

    /*init {
        _isDocumentPresent.bind(editorService.activeDocument.isNotNull)

        editorService.activeDocument.addListener { _, _, newDoc -> updateLockBinding(newDoc) }
        updateLockBinding(editorService.activeDocument.get())
    }

    private fun updateLockBinding(document: EditorDocument?) {
        _isDocumentLocked.unbind()
        if (document != null) {
            _isDocumentLocked.bind(document.isLocked)
        } else {
            _isDocumentLocked.set(false)
        }
    }

    fun toggleFileLock() {
        editorService.toggleFileLock()
    }*/
}