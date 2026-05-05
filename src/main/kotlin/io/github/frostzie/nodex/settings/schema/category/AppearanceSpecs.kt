package io.github.frostzie.nodex.settings.schema.category

import io.github.frostzie.nodex.domain.settings.SettingConstraints
import io.github.frostzie.nodex.domain.settings.SettingUiType
import io.github.frostzie.nodex.domain.settings.SettingUiType.ComboBoxOption
import io.github.frostzie.nodex.domain.settings.SettingValueType
import io.github.frostzie.nodex.domain.settings.category.ThemeOption
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.settings.schema.SettingsCategories

class AppearanceSpecs {
    val specs: List<SettingSpec> = listOf(
        SettingSpec(
            id = "appearance.theme",
            categoryId = SettingsCategories.APPEARANCE,
            valueType = SettingValueType.ENUM,
            uiType = SettingUiType.ComboBox(
                ThemeOption.entries.map { option ->
                    ComboBoxOption(displayLabel = option.displayName, storageValue = option.name)
                }
            ),
            title = "Theme",
            description = "The visual theme of the application.",
            enumValues = ThemeOption.entries.map { it.name }.toSet(),
            defaultGetter = { it.appearance.theme.name },
            valueSetter = { settings, value ->
                val name = value as String
                val theme = runCatching { ThemeOption.valueOf(name) }.getOrDefault(ThemeOption.PRIMER_DARK)
                settings.copy(appearance = settings.appearance.copy(theme = theme))
            }
        ),
        SettingSpec(
            id = "appearance.fontSize",
            categoryId = SettingsCategories.APPEARANCE,
            valueType = SettingValueType.INT,
            uiType = SettingUiType.Spinner(min = 8, max = 32),
            constraints = SettingConstraints(minNumeric = 8.0, maxNumeric = 32.0),
            title = "Font Size",
            description = "The base font size for the user interface.",
            defaultGetter = { it.appearance.fontSize },
            valueSetter = { settings, value ->
                settings.copy(appearance = settings.appearance.copy(fontSize = (value as Number).toInt()))
            }
        )
    )
}
