package io.github.frostzie.nodex.api.settings

import io.github.frostzie.nodex.domain.settings.AppSettings

/**
 * Contract for reading and staging settings.
 *
 * @see io.github.frostzie.nodex.services.settings.SettingsService
 * @see Settings
 */
interface SettingsAccess {
    /** The currently committed (saved) settings. */
    val committed: AppSettings

    /** Updates staged settings by applying [transform] to the current draft. */
    fun stage(transform: (AppSettings) -> AppSettings)
}
