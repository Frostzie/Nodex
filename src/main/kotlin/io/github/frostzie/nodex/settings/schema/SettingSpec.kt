package io.github.frostzie.nodex.settings.schema

import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.domain.settings.SettingConstraints
import io.github.frostzie.nodex.domain.settings.SettingUiType
import io.github.frostzie.nodex.domain.settings.SettingValueType

/**
 * Metadata for a single setting.
 *
 * @property id Globally unique identifier.
 * @property categoryId Which category this setting belongs to.
 * @property valueType The JSON type of the value.
 * @property uiType Which JavaFX control to render.
 * @property constraints Input limits (length, range, etc.).
 * @property title Readable label.
 * @property description Readable description..
 * @property enumValues Valid values for ENUM types.
 * @property defaultGetter Reads the current value from [AppSettings].
 * @property valueSetter Writes an updated value back into [AppSettings].
 */
data class SettingSpec(
    val id: String,
    val categoryId: String,
    val valueType: SettingValueType,
    val uiType: SettingUiType,
    val constraints: SettingConstraints = SettingConstraints(),
    val title: String,
    val description: String? = null,
    val enumValues: Set<String> = emptySet(),
    val defaultGetter: (AppSettings) -> Any,
    val valueSetter: (AppSettings, Any) -> AppSettings
)
