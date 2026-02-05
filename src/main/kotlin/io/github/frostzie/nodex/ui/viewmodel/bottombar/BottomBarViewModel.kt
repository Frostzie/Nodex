package io.github.frostzie.nodex.ui.viewmodel.bottombar

import io.github.frostzie.nodex.domain.entity.EditorDocument
import io.github.frostzie.nodex.services.core.ModInfoService
import io.github.frostzie.nodex.services.workspace.EditorService
import io.github.frostzie.nodex.ui.util.PerformanceTracker
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class BottomBarViewModel {
    val ideVersionProperty: StringProperty = SimpleStringProperty("Nodex v${ModInfoService.modInfo.version}")

    private val performanceTracker = PerformanceTracker()
    val fpsProperty: ReadOnlyStringProperty get() = performanceTracker.fpsProperty
    val memoryProperty: ReadOnlyStringProperty get() = performanceTracker.memoryProperty

    private val _isDocumentLocked = ReadOnlyBooleanWrapper(false)
    val isDocumentLocked: ReadOnlyBooleanProperty = _isDocumentLocked.readOnlyProperty

    private val _isDocumentPresent = ReadOnlyBooleanWrapper(false)
    val isDocumentPresent: ReadOnlyBooleanProperty = _isDocumentPresent.readOnlyProperty

    init {
        performanceTracker.start()

        _isDocumentPresent.bind(EditorService.activeDocument.isNotNull)

        EditorService.activeDocument.addListener { _, _, newDoc -> updateLockBinding(newDoc) }
        updateLockBinding(EditorService.activeDocument.get())
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
        EditorService.toggleFileLock()
    }
}