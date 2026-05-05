package io.github.frostzie.nodex.settings.schema.category

import io.github.frostzie.nodex.domain.entity.RgbaColor
import io.github.frostzie.nodex.domain.settings.SettingConstraints
import io.github.frostzie.nodex.domain.settings.SettingUiType
import io.github.frostzie.nodex.domain.settings.SettingUiType.ComboBoxOption
import io.github.frostzie.nodex.domain.settings.SettingValueType
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.settings.schema.SettingsCategories
import io.github.frostzie.nodex.domain.settings.category.ShowcaseMode

object ShowcaseSpecs {
    val specs: List<SettingSpec> = listOf(
        SettingSpec(
            id = "showcase.enabled",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.BOOLEAN,
            uiType = SettingUiType.Toggle,
            title = "Enabled",
            description = "Demo toggle value.",
            defaultGetter = { it.showcase.enabled },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(enabled = value as Boolean))
            }
        ),
        SettingSpec(
            id = "showcase.maxItems",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.INT,
            uiType = SettingUiType.Spinner(min = 1, max = 500, 1),
            constraints = SettingConstraints(minNumeric = 1.0, maxNumeric = 500.0),
            title = "Max Items",
            description = "Demo integer value.",
            defaultGetter = { it.showcase.maxItems },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(maxItems = (value as Number).toInt()))
            }
        ),
        SettingSpec(
            id = "showcase.opacity",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.DOUBLE,
            uiType = SettingUiType.Slider(min = 0.1, max = 1.0, step = 0.05),
            constraints = SettingConstraints(minNumeric = 0.1, maxNumeric = 1.0),
            title = "Opacity",
            description = "Demo double value.",
            defaultGetter = { it.showcase.opacity },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(opacity = (value as Number).toDouble()))
            }
        ),
        SettingSpec(
            id = "showcase.displayName",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.STRING,
            uiType = SettingUiType.TextField,
            constraints = SettingConstraints(maxLength = 64),
            title = "Display Name",
            description = "Demo string value.",
            defaultGetter = { it.showcase.displayName },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(displayName = value as String))
            }
        ),
        SettingSpec(
            id = "showcase.mode",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.ENUM,
            uiType = SettingUiType.ComboBox(
                ShowcaseMode.entries.map { mode ->
                    ComboBoxOption(displayLabel = mode.displayName, storageValue = mode.name)
                }
            ),
            title = "Mode",
            description = "Demo enum value.",
            enumValues = ShowcaseMode.entries.map { it.name }.toSet(),
            defaultGetter = { it.showcase.mode.name },
            valueSetter = { settings, value ->
                val name = value as String
                val mode = runCatching { ShowcaseMode.valueOf(name) }.getOrDefault(ShowcaseMode.BALANCED)
                settings.copy(showcase = settings.showcase.copy(mode = mode))
            }
        ),
        SettingSpec(
            id = "showcase.accentColor",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.COLOR,
            uiType = SettingUiType.ColorPicker,
            title = "Accent Color",
            description = "Demo color value stored as RGBA.",
            defaultGetter = { it.showcase.accentColor },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(accentColor = value as RgbaColor))
            }
        ),
        SettingSpec(
            id = "showcase.theme",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.INT,
            uiType = SettingUiType.Spinner(min = 11, max = 500),
            constraints = SettingConstraints(minNumeric = 11.0, maxNumeric = 500.0),
            title = "Theme",
            description = "Demo integer value.",
            defaultGetter = { it.showcase.theme },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(theme = (value as Number).toInt()))
            }
        ),
        SettingSpec(
            id = "showcase.fontSize",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.INT,
            uiType = SettingUiType.Spinner(min = -1, max = 1000, 10),
            constraints = SettingConstraints(minNumeric = -1.0, maxNumeric = 2000.0),
            title = "Font Size",
            description = "Demo integer value.",
            defaultGetter = { it.showcase.fontSize },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(fontSize = (value as Number).toInt()))
            }
        ),
        SettingSpec(
            id = "showcase.testString",
            categoryId = SettingsCategories.SHOWCASE,
            valueType = SettingValueType.STRING,
            uiType = SettingUiType.TextField,
            constraints = SettingConstraints(maxLength = 50),
            title = "Max String",
            description = "Demo String value.",
            defaultGetter = { it.showcase.testString },
            valueSetter = { settings, value ->
                settings.copy(showcase = settings.showcase.copy(testString = value as String))
            }
        )
    )
}
