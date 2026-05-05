package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.settings.registry.CoreCategories
import io.github.frostzie.nodex.settings.registry.SettingsRegistry

/**
 * Responsible for initializing settings registry.
 */
object SettingsBootstrap {
    lateinit var settingsRegistry: SettingsRegistry
        private set

    fun start() {
        settingsRegistry = SettingsRegistry().apply {
            CoreCategories.categories.forEach { registerCategory(it) }
            CoreCategories.specOwners.forEach { (ownerId, specs) ->
                if (specs.isNotEmpty()) registerSpecs(ownerId, specs)
            }
        }
    }
}
