package io.github.frostzie.nodex.ui.viewmodel.settings

import io.github.frostzie.nodex.domain.settings.AppSettings
import javafx.beans.property.BooleanProperty

interface SettingsPanelViewModel {
    val categoryId: String
    val isDirty: BooleanProperty
    val isValid: BooleanProperty

    fun initializeFromSettings(settings: AppSettings)
    fun applyChanges()
    fun discardChanges()

    fun validate(): Boolean = isValid.get()
}
