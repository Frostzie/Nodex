package io.github.frostzie.nodex.settings.registry

/**
 * Sets the metadata for a settings category node in the tree.
 *
 * Categories form a hierarchy via [parentId]. Root-level categories have `parentId = null`.
 * Leaf categories ([isLeaf] = true) have an associated settings panel.
 *
 * @property id Unique identifier for this category.
 * @property label Display name shown in the settings tree.
 * @property parentId ID of the parent category, or `null` for root-level categories.
 * @property isLeaf Whether this category has an associated settings panel.
 * @property storeId Settings store this category belongs to (currently core-only).
 */
data class CategorySpec(
    val id: String,
    val label: String,
    val parentId: String? = null,
    val isLeaf: Boolean = false,
    val storeId: String = SettingsStores.CORE
)
