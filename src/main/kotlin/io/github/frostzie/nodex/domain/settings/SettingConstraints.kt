package io.github.frostzie.nodex.domain.settings

/**
 * Optional, limits for settings inputs.
 */
data class SettingConstraints(
    val minLength: Int? = null, //TODO: Do not use until a way to inform it can't be empty is added
    val maxLength: Int? = null,
    val minNumeric: Double? = null,
    val maxNumeric: Double? = null,
    val regex: String? = null,
    val required: Boolean = false
)
