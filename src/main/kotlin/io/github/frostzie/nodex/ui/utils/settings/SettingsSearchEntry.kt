package io.github.frostzie.nodex.ui.utils.settings

import io.github.frostzie.nodex.settings.schema.SettingSpec

/**
 * Searchable metadata for a settings tile.
 */
data class SettingsSearchEntry(
    val id: String,
    val title: String,
    val description: String? = null
) {
    fun matches(query: String): Boolean {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return true

        val stack = buildString {
            append(title.lowercase())
            description?.let { append(' ').append(it.lowercase()) }
        }

        return normalized.split(Regex("\\s+")).all { token -> stack.contains(token) }
    }

    companion object {
        /**
         * Creates a [SettingsSearchEntry] from a [SettingSpec].
         */
        fun fromSpec(spec: SettingSpec): SettingsSearchEntry =
            SettingsSearchEntry(spec.id, spec.title, spec.description)
    }
}