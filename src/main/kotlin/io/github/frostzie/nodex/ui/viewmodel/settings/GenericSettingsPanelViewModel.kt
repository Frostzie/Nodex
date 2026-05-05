package io.github.frostzie.nodex.ui.viewmodel.settings

import io.github.frostzie.nodex.api.settings.SettingsAccess
import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.domain.entity.RgbaColor
import io.github.frostzie.nodex.domain.settings.SettingValueType
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.settings.schema.SettingSpec
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

/**
 * Settings panel ViewModel.
 *
 * Automatically creates JavaFX properties from [SettingSpec] definitions
 * and wires them up for dirty tracking, validation, and initialization.
 *
 * On [applyChanges], each spec's [SettingSpec.valueSetter] is invoked to
 * rebuild the [AppSettings].
 *
 * @param categoryId The settings category this panel manages.
 * @param registry Dynamic settings registry for spec lookups.
 */
class GenericSettingsPanelViewModel(
    override val categoryId: String,
    settingsService: SettingsAccess,
    registry: SettingsRegistry,
) : BaseSettingsPanelViewModel(categoryId, settingsService, registry) {

    /**
     * Dynamically created JavaFX properties, keyed by spec ID.
     */
    val properties: Map<String, Property<*>> = run {
        val map = mutableMapOf<String, Property<*>>()
        val specs = registry.specsByCategory(categoryId)
        for (spec in specs) {
            val property = createPropertyForSpec(spec)
            map[spec.id] = property
            registerWithSetter(spec.id, property, spec)
        }
        map
    }

    private fun createPropertyForSpec(spec: SettingSpec): Property<*> {
        return when (spec.valueType) {
            SettingValueType.BOOLEAN -> SimpleBooleanProperty()
            SettingValueType.INT -> SimpleIntegerProperty()
            SettingValueType.DOUBLE -> SimpleDoubleProperty()
            SettingValueType.STRING -> SimpleStringProperty()
            SettingValueType.ENUM -> SimpleStringProperty()
            SettingValueType.COLOR -> SimpleObjectProperty<RgbaColor>()
        }
    }

    private fun registerWithSetter(specId: String, property: Property<*>, spec: SettingSpec) {
        when (spec.valueType) {
            SettingValueType.BOOLEAN -> register(specId, property as BooleanProperty) {
                property.set(it as Boolean)
            }

            SettingValueType.INT -> register(specId, property as IntegerProperty) {
                property.set((it as Number).toInt())
            }

            SettingValueType.DOUBLE -> register(specId, property as DoubleProperty) {
                property.set((it as Number).toDouble())
            }

            SettingValueType.STRING -> register(specId, property as StringProperty) {
                property.set(it as String)
            }

            SettingValueType.ENUM -> register(specId, property as StringProperty) {
                property.set(it as String)
            }

            SettingValueType.COLOR ->
                @Suppress("UNCHECKED_CAST")
                register(specId, property as ObjectProperty<RgbaColor>) {
                    property.set(it as RgbaColor)
                }
        }
    }

    override fun applyChanges() {
        settingsService.stage { settings ->
            var updated = settings
            for (spec in registry.specsByCategory(categoryId)) {
                val property = properties[spec.id]
                    ?: error("Missing property for spec '${spec.id}' in category '$categoryId'")
                val value = property.value
                    ?: spec.defaultGetter(updated) // fallback if not yet initialized
                updated = spec.valueSetter(updated, value)
            }
            updated
        }
        isDirty.set(false)
    }
}
