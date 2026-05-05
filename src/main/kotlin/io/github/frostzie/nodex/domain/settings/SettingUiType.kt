package io.github.frostzie.nodex.domain.settings

/**
 * UI control type for a setting.
 *
 * [ComboBox] uses [ComboBoxOption] to separate display labels from storage values.
 */
sealed class SettingUiType {
    object Toggle : SettingUiType()
    object TextField : SettingUiType()
    data class Spinner(val min: Int, val max: Int, val step: Int = 1) : SettingUiType()
    data class Slider(val min: Double, val max: Double, val step: Double = 1.0) : SettingUiType()
    data class ComboBox(val options: List<ComboBoxOption>) : SettingUiType()
    object ColorPicker : SettingUiType()

    /**
     * A single option in a combo box setting.
     *
     * @property displayLabel Text shown to the user in the dropdown.
     * @property storageValue Value persisted to settings JSON.
     */
    data class ComboBoxOption(
        val displayLabel: String,
        val storageValue: String
    )
}
