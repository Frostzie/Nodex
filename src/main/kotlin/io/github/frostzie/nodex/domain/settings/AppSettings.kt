package io.github.frostzie.nodex.domain.settings

import io.github.frostzie.nodex.domain.settings.category.AppearanceSettings
import io.github.frostzie.nodex.domain.settings.category.FileTreeSettings
import io.github.frostzie.nodex.domain.settings.category.ShowcaseSettings

data class AppSettings(
    val showcase: ShowcaseSettings = ShowcaseSettings(),
    val appearance: AppearanceSettings = AppearanceSettings(),
    val fileTree: FileTreeSettings = FileTreeSettings()
)
