package io.github.frostzie.nodex.ui.viewmodel.settings

/**
 * Represents a node in the settings category tree.
 */
data class SettingsCategoryNode(
    val id: String,
    val label: String,
    val panelId: String? = null,
    val children: List<SettingsCategoryNode> = emptyList(),
    val searchable: Boolean = true
)
