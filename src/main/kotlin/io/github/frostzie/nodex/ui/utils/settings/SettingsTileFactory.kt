package io.github.frostzie.nodex.ui.utils.settings

import atlantafx.base.controls.Tile
import atlantafx.base.controls.ToggleSwitch
import io.github.frostzie.nodex.domain.entity.RgbaColor
import io.github.frostzie.nodex.domain.settings.SettingUiType
import io.github.frostzie.nodex.domain.settings.SettingUiType.ComboBoxOption
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.ui.utils.ColorUtils
import io.github.frostzie.nodex.ui.utils.extensions.withMaxLength
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.layout.Region
import javafx.util.Callback

/**
 * Factory for building settings tiles with their controls fully wired.
 *
 * Each builder accepts the [SettingSpec] for metadata (title, description, constraints)
 * and the ViewModel property for binding. The returned [Tile] is ready
 * to be added to a settings panel's children list.
 */
object SettingsTileFactory {
    private val logger = LoggerProvider.getLogger("SettingsTileFactory")

    fun buildToggle(
        spec: SettingSpec,
        property: BooleanProperty
    ): Tile {
        val control = ToggleSwitch().apply {
            selectedProperty().bindBidirectional(property)
        }
        return wrapInTile(spec, control)
    }

    fun buildSpinner(
        spec: SettingSpec,
        property: IntegerProperty
    ): Tile {
        val uiType = spec.uiType as? SettingUiType.Spinner
            ?: return skipTile("Expected Spinner uiType for '${spec.id}', got ${spec.uiType::class.simpleName}")
        val control = Spinner<Int>().apply {
            valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                uiType.min,
                uiType.max,
                property.get(),
                uiType.step
            )
            isEditable = false //TODO: Eventually enable once possible to not allow non numbers to be input
            valueFactory.valueProperty().bindBidirectional(property.asObject())
        }
        return wrapInTile(spec, control)
    }

    fun buildSlider(
        spec: SettingSpec,
        property: DoubleProperty
    ): Tile {
        val uiType = spec.uiType as? SettingUiType.Slider
            ?: return skipTile("Expected Slider uiType for '${spec.id}', got ${spec.uiType::class.simpleName}")
        val control = Slider(uiType.min, uiType.max, property.get()).apply {
            majorTickUnit = uiType.step
            blockIncrement = uiType.step
            isShowTickMarks = true
            isShowTickLabels = true
            valueProperty().bindBidirectional(property)
        }
        return wrapInTile(spec, control)
    }

    fun buildTextField(
        spec: SettingSpec,
        property: StringProperty
    ): Tile {
        val maxLength = spec.constraints.maxLength
        val control = TextField().apply {
            promptText = spec.description ?: ""
            if (maxLength != null) withMaxLength(maxLength)
            textProperty().bindBidirectional(property)
        }
        return wrapInTile(spec, control)
    }

    fun buildColorPicker(
        spec: SettingSpec,
        property: ObjectProperty<RgbaColor>
    ): Tile {
        val defaultColor = ColorUtils.rgbaToColor(property.get())
        val control = ColorPicker(defaultColor).apply {
            valueProperty().addListener { _, _, newColor ->
                property.set(ColorUtils.colorToRgba(newColor))
            }
        }
        return wrapInTile(spec, control)
    }

    fun buildComboBox(
        spec: SettingSpec,
        property: StringProperty,
        options: List<ComboBoxOption>
    ): Tile {
        val control = ComboBox<ComboBoxOption>().apply {
            items.setAll(options)
            value = options.find { it.storageValue == property.get() } ?: options.firstOrNull()
            prefWidth = 180.0

            // Show displayLabel, not storageValue
            cellFactory = Callback {
                ListCell<ComboBoxOption>().apply {
                    itemProperty().addListener { _, _, new ->
                        text = new?.displayLabel
                    }
                }
            }
            buttonCell = object : ListCell<ComboBoxOption>() {
                override fun updateItem(item: ComboBoxOption?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null else item.displayLabel
                }
            }
        }

        // On selection, write storageValue back to the property
        control.valueProperty().addListener { _, _, new ->
            if (new != null) {
                property.set(new.storageValue)
            }
        }

        return wrapInTile(spec, control)
    }

    /**
     * Wraps an already-built control in a Tile.
     * Use the typed builders above when possible.
     */
    fun create(spec: SettingSpec, control: Node, actionHandler: Runnable? = null): Tile {
        return wrapInTile(spec, control, actionHandler)
    }

    private fun wrapInTile(spec: SettingSpec, control: Node, actionHandler: Runnable? = null): Tile {
        return Tile(spec.title, spec.description ?: "").apply {
            styleClass.add("settings-tile")
            action = control
            actionHandler?.let { setActionHandler(it) }
            minHeight = Region.USE_PREF_SIZE
        }
    }

    /**
     * Logs a mismatch and returns an invisible node.
     */
    private fun skipTile(message: String): Tile {
        logger.error(message)
        return Tile().apply {
            isVisible = false
            isManaged = false
        }
    }
}