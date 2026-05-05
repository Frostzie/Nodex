package io.github.frostzie.nodex.services.config.stationary

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path

/**
 * Service managing the stationary configuration file (nodex.json).
 * Contains universal paths and version tracking.
 */
class ConfigService(
    configRoot: Path,
    private val fileOps: FileOperations,
    private val modVersion: ModVersion
) : Config {
    private val logger = LoggerProvider.getLogger("ConfigService")
    private val mapper = ObjectMapper().registerKotlinModule()
    private val configPath = configRoot.resolve("nodex").resolve("nodex.json")

    private var _lastUsedModVersion: String = "0.0.0"
    private var _universalPathEnabled: Boolean = false
    private var _universalPath: String? = null
    private var _introFinished: Boolean = false

    override val lastUsedModVersion: String get() = _lastUsedModVersion
    override val universalPathEnabled: Boolean get() = _universalPathEnabled
    override val universalPath: String? get() = _universalPath
    override val introFinished: Boolean get() = _introFinished

    private data class StationaryConfig(
        val modVersion: String,
        val universalPathEnabled: Boolean = false,
        val universalPath: String? = null,
        val introFinished: Boolean = false
    )

    /**
     * Loads from nodex.json or creates defaults if missing.
     */
    override fun initialize() {
        if (fileOps.exists(configPath)) {
            load()
        } else {
            createDefault()
        }
    }

    private fun load() {
        try {
            val json = fileOps.readText(configPath)
            val config = mapper.readValue(json, StationaryConfig::class.java)

            _lastUsedModVersion = config.modVersion
            _universalPathEnabled = config.universalPathEnabled
            _universalPath = config.universalPath
            _introFinished = config.introFinished

            logger.info("Loaded stationary config. Last version recorded: $_lastUsedModVersion")
        } catch (e: Exception) {
            logger.error("Failed to load stationary config, falling back to defaults", e)
            createDefault()
        }
    }

    private fun createDefault() {
        _lastUsedModVersion = modVersion.currentVersion
        _universalPathEnabled = false
        _universalPath = null
        _introFinished = false
        save()
    }

    /**
     * Marks that settings have been successfully loaded and migrated.
     * Updates the modVersion to current one.
     */
    override fun markSettingsLoadCompleted() {
        if (_lastUsedModVersion != modVersion.currentVersion) {
            logger.info("Settings load completed. Updating recorded version to ${modVersion.currentVersion}")
            _lastUsedModVersion = modVersion.currentVersion
            save()
        }
    }

    /**
     * Marks the intro view as completed.
     */
    override fun markIntroCompleted() {
        _introFinished = true
        save()
    }

    /**
     * Saves the current state to nodex.json.
     */
    override fun save() {
        try {
            val config = StationaryConfig(
                modVersion = _lastUsedModVersion,
                universalPathEnabled = _universalPathEnabled,
                universalPath = _universalPath,
                introFinished = _introFinished
            )
            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config)
            fileOps.writeAtomic(configPath, json)
        } catch (e: Exception) {
            logger.error("Failed to save stationary config", e)
        }
    }
}
