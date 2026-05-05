package io.github.frostzie.nodex.services.config.project

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.frostzie.nodex.domain.config.TreeConfig
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

/**
 * Service for reading and writing to `.nodex/tree.json` folder.
 */
class TreeConfigService(private val fileOps: FileOperations) {
    private val logger = LoggerProvider.getLogger("TreeConfigService")
    private val mapper = jacksonObjectMapper()

    fun load(projectRoot: Path): TreeConfig {
        val configPath = getConfigPath(projectRoot)
        if (!fileOps.exists(configPath)) {
            return TreeConfig()
        }

        return try {
            val json = fileOps.readText(configPath)
            mapper.readValue(json)
        } catch (e: Exception) {
            logger.warn("Failed to parse tree config at $configPath. Returning empty config.", e)
            TreeConfig()
        }
    }

    fun save(projectRoot: Path, config: TreeConfig) {
        val nodexDir = projectRoot.resolve(".nodex")
        val configPath = nodexDir.resolve("tree.json")

        try {
            if (!fileOps.exists(nodexDir)) {
                fileOps.createDirectory(nodexDir)
            }
            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config)
            fileOps.writeAtomic(configPath, json)
        } catch (e: Exception) {
            logger.error("Failed to save tree config to $configPath", e)
        }
    }

    fun toStoragePath(root: Path, path: Path): String? {
        if (!path.toAbsolutePath().startsWith(root.toAbsolutePath())) {
            logger.warn("Path $path is not under root $root. Skipping.")
            return null
        }
        return try {
            path.relativeTo(root).invariantSeparatorsPathString
        } catch (e: Exception) {
            logger.warn("Failed to relativize path $path to root $root. Skipping.", e)
            null
        }
    }

    fun fromStoragePath(root: Path, stored: String): Path {
        return root.resolve(stored)
    }

    private fun getConfigPath(projectRoot: Path): Path {
        return projectRoot.resolve(".nodex").resolve("tree.json")
    }
}
