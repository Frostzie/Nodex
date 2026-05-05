package io.github.frostzie.nodex.api.misc

/**
 * Provides information about the current mod version.
 *
 * @see io.github.frostzie.nodex.services.core.ModVersionService
 */
interface ModVersion {
    /**
     * Initializes the service by fetching the mod version from the loader.
     */
    fun initialize()

    /**
     * The current version of the mod.
     */
    val currentVersion: String
}
