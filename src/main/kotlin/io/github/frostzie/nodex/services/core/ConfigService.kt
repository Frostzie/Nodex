package io.github.frostzie.nodex.services.core

import com.google.gson.GsonBuilder
import io.github.frostzie.nodex.loader.fabric.Folders
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

/**
 * Service that manages the application's config directory.
 *
 * It determines where config files (layout, session, etc.) are stored.
 * By default, this is in the Fabric config folder, but it can be redirected
 * via a 'config_location.json' file to support universal setups.
 */
object ConfigService {
    private val logger = LoggerProvider.getLogger("ConfigService")
    private val gson = GsonBuilder().setPrettyPrinting().create()

    // The default location: <instance>/config/nodex
    private val defaultConfigDir: Path = Folders.configDir.resolve("nodex")

    // File that determines the actual config location
    private val locationFile: Path = defaultConfigDir.resolve("config_location.json")

    // The active config directory
    private var _configDir: Path? = null
    val configDir: Path
        get() {
            if (_configDir == null) _configDir = resolveConfigDir()
            return _configDir!!
        }

    // DTO for the location file
    private data class LocationConfig(var customConfigPath: String? = null)

    fun initialize() {
        if (!configDir.exists()) {
            try {
                Files.createDirectories(configDir)
                logger.debug("Created config directory: {}", configDir)
            } catch (e: Exception) {
                logger.error("Failed to create config directory: $configDir", e)
            }
        }
        logger.info("ConfigService initialized. Root: {}", configDir)
    }

    fun reload() {
        _configDir = resolveConfigDir()
        if (!configDir.exists()) {
            try {
                Files.createDirectories(configDir)
            } catch (e: Exception) {
                logger.error("Failed to create config directory on reload: $configDir", e)
            }
        }
        logger.debug("Config directory reloaded: {}", configDir)
    }

    /**
     * Sets a custom configuration path and saves it to the locationFile.
     * Passing null reverts to the default directory.
     */
    fun setCustomConfigPath(path: Path?) {
        try {
            if (!defaultConfigDir.exists()) {
                Files.createDirectories(defaultConfigDir)
            }

            val config = LocationConfig(path?.toAbsolutePath()?.toString())
            val json = gson.toJson(config)

            Files.writeString(locationFile, json)

            logger.info("Updated config location. New path: {}", path ?: "Default")
            
            // Invalidate cache
            _configDir = null
        } catch (e: Exception) {
            logger.error("Failed to save config location file.", e)
        }
    }

    /**
     * @return the resolved config `Path` to use for saving configs.
     */
    private fun resolveConfigDir(): Path {
        if (!defaultConfigDir.exists()) {
            try {
                Files.createDirectories(defaultConfigDir)
            } catch (e: Exception) {
                logger.error("Failed to create default config dir: $defaultConfigDir", e)
                return defaultConfigDir
            }
        }

        if (Files.exists(locationFile)) {
            try {
                val json = Files.readString(locationFile)
                val config = gson.fromJson(json, LocationConfig::class.java)
                if (!config.customConfigPath.isNullOrBlank()) {
                    val customPath = Paths.get(config.customConfigPath!!)
                    logger.debug("Using custom config directory: {}", customPath)
                    return customPath
                }
            } catch (e: Exception) {
                logger.error("Failed to read config location file, falling back to default.", e)
            }
        }
        return defaultConfigDir
    }
}
