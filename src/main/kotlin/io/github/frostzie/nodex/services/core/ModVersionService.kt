package io.github.frostzie.nodex.services.core

import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.domain.entity.ModInfo
import io.github.frostzie.nodex.loader.fabric.ModVersion as ModVersionLoader

/**
 * Service providing information about the current version of the mod.
 */
class ModVersionService : ModVersion {
    /**
     * Initializes the service by fetching the mod version from the loader.
     */
    override fun initialize() {
        ModInfo.version = ModVersionLoader.getModVersion()
    }

    /**
     * The current version of the mod.
     */
    override val currentVersion: String get() = ModInfo.version
}
