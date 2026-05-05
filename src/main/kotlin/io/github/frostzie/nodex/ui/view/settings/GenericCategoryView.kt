package io.github.frostzie.nodex.ui.view.settings

import io.github.frostzie.nodex.domain.entity.RgbaColor
import io.github.frostzie.nodex.domain.settings.SettingUiType
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.ui.utils.settings.SettingsSearchEntry
import io.github.frostzie.nodex.ui.utils.settings.SettingsTileFactory
import io.github.frostzie.nodex.ui.viewmodel.settings.GenericSettingsPanelViewModel
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox

/**
 * Builds tiles from [SettingsRegistry] specs using [SettingsTileFactory],
 * filtered by [searchQuery]. Shows an empty-state label when no tiles match.
 *
 * @param viewModel The generic panel ViewModel.
 * @param searchQuery Search query string property.
 * @param registry Settings registry for spec lookups.
 */
class GenericCategoryView(
    private val viewModel: GenericSettingsPanelViewModel,
    private val searchQuery: StringProperty,
    private val registry: SettingsRegistry
) : VBox(12.0) {

    private val emptyLabel = Label("No settings match your search.")

    init {
        styleClass.add("settings-panel")
        padding = Insets(12.0)
        children.add(emptyLabel)

        searchQuery.addListener { _, _, _ -> rebuildTiles() }
        rebuildTiles()
    }

    /**
     * Clears and rebuilds all tiles from current registry specs.
     */ //TODO: Improve (Make them hide instead of rebuild + dynamic loading support)
    private fun rebuildTiles() {
        children.removeAll { it !== emptyLabel }

        val specs = registry.specsByCategory(viewModel.categoryId)
        val searchEntries = specs.associateBy({ it.id }, { SettingsSearchEntry.fromSpec(it) })
        val query = searchQuery.get()

        val matchingTiles = specs.mapNotNull { spec ->
            val entry = searchEntries[spec.id]!!
            if (!entry.matches(query)) return@mapNotNull null
            spec.id to buildTileForSpec(spec)
        }.toMap()

        children.addAll(matchingTiles.values)

        val anyVisible = matchingTiles.isNotEmpty()
        val searching = query.isNotBlank()
        emptyLabel.isVisible = searching && !anyVisible
        emptyLabel.isManaged = searching && !anyVisible
    }

    private fun buildTileForSpec(spec: SettingSpec): Node {
        val property = viewModel.properties[spec.id]
            ?: return SettingsTileFactory.create(spec, Label("Missing property for ${spec.id}"))

        return when (val uiType = spec.uiType) {
            is SettingUiType.Toggle -> {
                SettingsTileFactory.buildToggle(spec, property as BooleanProperty)
            }

            is SettingUiType.Spinner -> {
                SettingsTileFactory.buildSpinner(spec, property as IntegerProperty)
            }

            is SettingUiType.Slider -> {
                SettingsTileFactory.buildSlider(spec, property as DoubleProperty)
            }

            is SettingUiType.TextField -> {
                SettingsTileFactory.buildTextField(spec, property as StringProperty)
            }

            is SettingUiType.ComboBox -> {
                SettingsTileFactory.buildComboBox(spec, property as StringProperty, uiType.options)
            }

            is SettingUiType.ColorPicker -> {
                @Suppress("UNCHECKED_CAST")
                SettingsTileFactory.buildColorPicker(spec, property as ObjectProperty<RgbaColor>)
            }
        }
    }
}
