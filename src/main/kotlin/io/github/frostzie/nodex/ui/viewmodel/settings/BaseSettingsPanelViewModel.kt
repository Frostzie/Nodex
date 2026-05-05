package io.github.frostzie.nodex.ui.viewmodel.settings

import io.github.frostzie.nodex.api.settings.SettingsAccess
import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.settings.validation.SettingsValidationRules
import javafx.beans.property.BooleanProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue

/**
 * Base class for settings panel ViewModels.
 *
 * @param categoryId The settings category this ViewModel manages.
 * @param registry Dynamic settings registry for spec lookups.
 */
abstract class BaseSettingsPanelViewModel(
    override val categoryId: String,
    protected val settingsService: SettingsAccess,
    protected val registry: SettingsRegistry
) : SettingsPanelViewModel {

    override val isDirty: BooleanProperty = SimpleBooleanProperty(false)
    override val isValid: BooleanProperty = SimpleBooleanProperty(true)
    val validationMessage: StringProperty = SimpleStringProperty("")

    private val propertyRegistry = mutableMapOf<String, RegisteredProperty>()
    protected fun <T : Any> register(
        specId: String,
        property: Property<T>,
        setter: (Any) -> Unit
    ) {
        propertyRegistry[specId] = RegisteredProperty(property, setter)
        property.addListener { _, _, _ -> onSettingChanged() }
    }

    fun onSettingChanged() {
        updateDirtyState()
        validate()
    }

    private fun updateDirtyState() {
        val committed = settingsService.committed
        val dirty = propertyRegistry.any { (specId, entry) ->
            val spec = registry.specById(specId) ?: return@any false
            val currentValue = (entry.property as? ObservableValue<*>)?.value
            val committedValue = spec.defaultGetter(committed)
            currentValue != committedValue
        }
        isDirty.set(dirty)
    }

    override fun validate(): Boolean {
        for ((specId, entry) in propertyRegistry) {
            val spec = registry.specById(specId) ?: continue
            val value = (entry.property as? ObservableValue<*>)?.value
            val result = SettingsValidationRules.validateValue(value, spec)
            if (!result.isValid) {
                validationMessage.set("${spec.title}: ${result.reason ?: "Invalid value"}")
                isValid.set(false)
                return false
            }
        }
        validationMessage.set("")
        isValid.set(true)
        return true
    }

    override fun initializeFromSettings(settings: AppSettings) {
        for (spec in registry.specsByCategory(categoryId)) {
            val entry = propertyRegistry[spec.id]
            if (entry != null) {
                val value = spec.defaultGetter(settings)
                entry.setter(value)
            }
        }
        isDirty.set(false)
        validate()
    }

    override fun discardChanges() {
        initializeFromSettings(settingsService.committed)
    }

    private data class RegisteredProperty(
        val property: Property<*>,
        val setter: (Any) -> Unit
    )
}
