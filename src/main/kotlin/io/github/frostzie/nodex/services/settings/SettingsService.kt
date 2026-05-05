package io.github.frostzie.nodex.services.settings

import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.domain.settings.ApplyResult
import io.github.frostzie.nodex.services.config.global.SettingsConfigService
import io.github.frostzie.nodex.utils.LoggerProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * State service for app settings.
 *
 * Keeps two immutable snapshots:
 * - committed: last persisted settings currently in effect.
 * - staged: draft edits from UI, persisted only on [apply].
 *
 * State is exposed as [StateFlow] for reactive consumption,
 * and as synchronous getters ([committed], [staged]) for simple reads.
 *
 * @param settingsConfigService Service for loading/saving settings to disk.
 */
class SettingsService(
    private val settingsConfigService: SettingsConfigService,
    private val validationService: SettingsValidationService
) : Settings {
    private val logger = LoggerProvider.getLogger("SettingsService")
    private var isInitialized = false

    private var _committedSettings = MutableStateFlow(AppSettings())
    private var _stagedSettings = MutableStateFlow(AppSettings())

    /**
     * Read-only flow of committed settings.
     *
     * Collect this to react to applied settings changes.
     * For simple synchronous reads, use [committed].
     */
    val committedSettingsFlow: StateFlow<AppSettings> = _committedSettings.asStateFlow()

    /**
     * Read-only flow of staged settings.
     *
     * Collect this to react to draft changes in the UI.
     * For simple synchronous reads, use [staged].
     */
    val stagedSettingsFlow: StateFlow<AppSettings> = _stagedSettings.asStateFlow()

    override fun initialize() {
        check(!isInitialized) { "SettingsService.initialize() can only be called once" }

        val loaded = try {
            settingsConfigService.load()
        } catch (e: Exception) {
            logger.error("Failed to load settings, using defaults.", e)
            AppSettings()
        }

        _committedSettings.value = loaded
        _stagedSettings.value = loaded.copy()
        isInitialized = true
        logger.debug("SettingsService initialized with: {}", loaded)
    }

    /**
     * Ensures the service has been initialized.
     */
    private fun checkInitialized() {
        check(isInitialized) {
            "SettingsService must be initialized before use. Call initialize() first."
        }
    }

    /**
     * The currently committed (saved) settings.
     *
     * These are the settings actively used.
     * For reactive consumption, use [committedSettingsFlow].
     */
    override val committed: AppSettings
        get() {
            checkInitialized()
            return _committedSettings.value
        }

    /**
     * The currently staged (draft) settings.
     *
     * These are the settings being edited in the UI, but not yet saved to configs.
     * For reactive consumption, use [stagedSettingsFlow].
     */
    val staged: AppSettings
        get() {
            checkInitialized()
            return _stagedSettings.value
        }

    /**
     * Updates staged settings by applying [transform] to the current draft.
     */
    override fun stage(transform: (AppSettings) -> AppSettings) {
        checkInitialized()
        _stagedSettings.value = transform(_stagedSettings.value)
    }

    /**
     * Applies staged settings as the new committed settings.
     *
     * Validates staged settings before committing. Returns [ApplyResult.Failure]
     * if validation errors are found (blocks the commit).
     */
    override fun apply(): ApplyResult {
        checkInitialized()

        val validationResult = validationService.validateStaged(_stagedSettings.value)
        if (validationResult.issues.isNotEmpty()) {
            logger.warn("Apply blocked: {} validation issue(s) found.", validationResult.issues.size)
            return ApplyResult.Failure(validationResult.issues)
        }

        val staged = _stagedSettings.value
        try {
            settingsConfigService.save(staged)
        } catch (e: Exception) {
            logger.error("Failed to save settings during apply.", e)
            throw e
        }
        _committedSettings.value = staged
        _stagedSettings.value = staged.copy()
        logger.debug("Settings applied and saved.")
        return ApplyResult.Success
    }

    /**
     * Discards staged changes, reverting to committed settings.
     */
    override fun discard() {
        checkInitialized()
        _stagedSettings.value = _committedSettings.value.copy()
        logger.debug("Staged settings discarded. Restored from committed.")
    }
}
