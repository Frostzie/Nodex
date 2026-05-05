package io.github.frostzie.nodex.api.config

/**
 * Manages the stationary configuration file (nodex.json).
 *
 * Stores settings that are not user editable: last mod version used,
 * universal path toggle and custom path override.
 *
 * @see io.github.frostzie.nodex.services.config.stationary.ConfigService
 */
interface Config {

    /** The mod version recorded when settings were last successfully loaded. */
    val lastUsedModVersion: String

    /** Whether a custom universal path is enabled. */
    val universalPathEnabled: Boolean

    /** Optional custom path override for the config root directory. */
    val universalPath: String?

    /** Has the intro screen been finished */
    val introFinished: Boolean

    /** Loads from nodex.json or creates defaults if missing. */
    fun initialize()

    /**
     * Marks that settings have been successfully loaded and migrated.
     * Updates the modVersion to current one.
     */
    fun markSettingsLoadCompleted()

    /**
     * Marks the intro view as completed.
     */
    fun markIntroCompleted()

    /** Saves the current state to nodex.json. */
    fun save()
}