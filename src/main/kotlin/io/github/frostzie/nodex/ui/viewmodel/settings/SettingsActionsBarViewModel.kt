package io.github.frostzie.nodex.ui.viewmodel.settings

import io.github.frostzie.nodex.domain.settings.ApplyResult
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

/**
 * ViewModel for the settings actions bar (Apply, Save, Discard buttons).
 */
class SettingsActionsBarViewModel(
    private val settingsService: Settings,
    private val featureViewModels: List<SettingsPanelViewModel>,
    private val navigationService: Navigation
) {
    private val logger = LoggerProvider.getLogger("SettingsActionsBarViewModel")

    val isDirty: BooleanProperty = SimpleBooleanProperty(false)
    val isValid: BooleanProperty = SimpleBooleanProperty(true)

    init {
        featureViewModels.forEach { vm ->
            vm.isDirty.addListener { _, _, _ -> updateAggregatedState() }
            vm.isValid.addListener { _, _, _ -> updateAggregatedState() }
        }
        updateAggregatedState()
    }

    private fun updateAggregatedState() {
        isDirty.set(featureViewModels.any { it.isDirty.get() })
        isValid.set(featureViewModels.all { it.isValid.get() })
    }

    /**
     * Applies the currently staged settings for all features.
     * Returns true if apply succeeded, false otherwise.
     */
    fun apply(): Boolean {
        if (!isDirty.get()) return true
        featureViewModels.forEach { it.applyChanges() }
        return when (val result = settingsService.apply()) {
            is ApplyResult.Success -> {
                updateAggregatedState()
                true
            }

            is ApplyResult.Failure -> {
                updateAggregatedState()
                val messages = result.issues.joinToString("; ") { "${it.path}: ${it.reason}" }
                logger.warn("Apply blocked: $messages")
                false
            }
        }
    }

    /**
     * Applies the staged settings, saves them, and closes the overlay.
     * Returns true if save succeeded, false otherwise.
     */
    fun save(): Boolean {
        val success = apply()
        if (success) {
            navigationService.closeOverlay()
        }
        return success
    }

    /**
     * Discards any staged changes for all features.
     */
    fun discard() {
        settingsService.discard()
        featureViewModels.forEach { it.discardChanges() }
    }
}
