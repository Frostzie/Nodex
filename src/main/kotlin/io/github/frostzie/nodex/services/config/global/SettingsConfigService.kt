package io.github.frostzie.nodex.services.config.global

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.services.settings.SettingsValidationService
import io.github.frostzie.nodex.services.config.BackupService
import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.config.Migration
import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import io.github.frostzie.nodex.utils.ModVersionUtils
import java.nio.file.Path

/**
 * Service responsible for reading and writing settings to disk.
 * Handles all JSON serialization/deserialization.
 */
class SettingsConfigService(
    private val settingsPath: Path,
    private val backupDir: Path,
    private val fileOps: FileOperations,
    private val configService: Config,
    private val migration: Migration,
    private val modVersion: ModVersion,
    private val backupService: BackupService,
    private val validationService: SettingsValidationService
) {
    private val logger = LoggerProvider.getLogger("SettingsConfigService")
    private val mapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    /**
     * Loads settings, applying migrations if needed.
     *
     * @return Loaded [AppSettings] or defaults if file doesn't exist or is corrupt.
     */
    fun load(): AppSettings {
        if (!fileOps.exists(settingsPath)) {
            logger.debug("Settings file not found at {}, using defaults.", settingsPath)
            configService.markSettingsLoadCompleted()
            return AppSettings()
        }

        return try {
            val json = fileOps.readText(settingsPath)
            val rootNode = mapper.readTree(json)

            val lastVersion = configService.lastUsedModVersion
            val currentVersion = modVersion.currentVersion

            val resultNode = if (ModVersionUtils.isOlderThan(lastVersion, currentVersion)) {
                logger.info("Migrating settings from $lastVersion to $currentVersion")
                migration.migrate(rootNode, lastVersion)
            } else {
                rootNode
            }

            val defaults = AppSettings()
            val validation = validationService.validate(resultNode, defaults)
            validation.issues.forEach { issue ->
                logger.warn(
                    "Settings validation: {} ({}) -> {}",
                    issue.path,
                    issue.reason,
                    issue.newValue
                )
            }

            if (validation.issues.isNotEmpty()) {
                logger.info(
                    "Checked settings file had {} invalid field(s), saving corrected values.",
                    validation.issues.size
                )
                save(parseSettings(validation.sanitizedNode))
            }

            val settings = parseSettings(validation.sanitizedNode)
            configService.markSettingsLoadCompleted()
            logger.debug("Settings loaded successfully from: {}", settingsPath)
            settings
        } catch (e: Exception) {
            logger.error("Failed to load settings, using defaults.", e)
            configService.markSettingsLoadCompleted()
            AppSettings()
        }
    }

    /**
     * Persists [settings] to disk.
     *
     * @param settings The settings to save.
     * @throws Exception if write fails (caller should handle).
     */
    fun save(settings: AppSettings) {
        try {
            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings)
            fileOps.writeAtomic(settingsPath, json)
            logger.debug("Settings saved successfully to: {}", settingsPath)

            backupService.backup(settingsPath, backupDir, 5)
        } catch (e: Exception) {
            logger.error("Failed to save settings to: $settingsPath", e)
            throw e
        }
    }

    private fun parseSettings(rootNode: JsonNode): AppSettings {
        return try {
            mapper.treeToValue(rootNode, AppSettings::class.java)
        } catch (e: Exception) {
            logger.warn("Failed to parse settings payload, falling back to defaults", e)
            AppSettings()
        }
    }
}
