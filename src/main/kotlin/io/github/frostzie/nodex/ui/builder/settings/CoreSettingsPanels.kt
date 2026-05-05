package io.github.frostzie.nodex.ui.builder.settings

import io.github.frostzie.nodex.settings.schema.SettingsCategories

object CoreSettingsPanels {
    val bindings = listOf(
        SettingsPanelBinding.generic(categoryId = SettingsCategories.SHOWCASE),
        SettingsPanelBinding.generic(categoryId = SettingsCategories.APPEARANCE)
    )
}
