package io.github.frostzie.nodex.api.settings

import io.github.frostzie.nodex.domain.settings.ApplyResult

/**
 * Lifecycle contract for settings management.
 * Extends [SettingsAccess] to expose read/staging capabilities.
 *
 * @see io.github.frostzie.nodex.services.settings.SettingsService
 */
interface Settings : SettingsAccess {

    /** Loads settings from file and initializes state. */
    fun initialize()

    /** Applies staged settings as the new committed state. */
    fun apply(): ApplyResult

    /** Discards staged changes, reverting to committed state. */
    fun discard()
}