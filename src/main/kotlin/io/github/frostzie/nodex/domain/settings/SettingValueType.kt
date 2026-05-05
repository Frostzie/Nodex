package io.github.frostzie.nodex.domain.settings

/**
 * JSON value type for a setting.
 */
enum class SettingValueType {
    BOOLEAN,
    INT,
    DOUBLE,
    STRING,
    ENUM,

    /** RGBA format */
    COLOR
}
