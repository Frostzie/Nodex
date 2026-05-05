package io.github.frostzie.nodex.settings.registry

import io.github.frostzie.nodex.settings.schema.category.AppearanceSpecs
import io.github.frostzie.nodex.settings.schema.category.ShowcaseSpecs

/**
 * Owner ID constants for core settings categories.
 */
object CoreSettingsOwners {
    const val SHOWCASE = "core:showcase"
    const val APPEARANCE = "core:appearance"
}

/**
 * Built-in category definitions for core settings.
 */
object CoreCategories {
    val categories = listOf(/*
        CategorySpec(
            id = "showcase",
            label = "Showcase",
            parentId = null,
            isLeaf = true,
            storeId = SettingsStores.CORE
        ),*/
        CategorySpec(
            id = "appearance",
            label = "Appearance",
            parentId = null,
            isLeaf = true,
            storeId = SettingsStores.CORE
        )
    )

    /**
     * Returns the spec-to-owner mapping for all core specs.
     */
    val specOwners = mapOf(
        //CoreSettingsOwners.SHOWCASE to ShowcaseSpecs.specs,
        CoreSettingsOwners.APPEARANCE to AppearanceSpecs().specs
    )
}
