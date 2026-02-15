package io.github.frostzie.nodex.ui.viewmodel.bottombar

import io.github.frostzie.nodex.domain.entity.EditorDocument
import io.github.frostzie.nodex.services.core.ModInfoService
import io.github.frostzie.nodex.services.core.PerformanceService
import io.github.frostzie.nodex.services.workspace.EditorService
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class BottomBarViewModel(
    private val modInfoService: ModInfoService,
    private val editorService: EditorService,
    private val performanceService: PerformanceService
) {
    val ideVersionProperty: StringProperty = SimpleStringProperty("Nodex v${modInfoService.modInfo.version}")

    val fpsProperty: ReadOnlyStringProperty get() = performanceService.fpsProperty
    val memoryProperty: ReadOnlyStringProperty get() = performanceService.memoryProperty

    private val _isDocumentLocked = ReadOnlyBooleanWrapper(false)
    val isDocumentLocked: ReadOnlyBooleanProperty = _isDocumentLocked.readOnlyProperty

    private val _isDocumentPresent = ReadOnlyBooleanWrapper(false)
    val isDocumentPresent: ReadOnlyBooleanProperty = _isDocumentPresent.readOnlyProperty

    init {
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
    }
}