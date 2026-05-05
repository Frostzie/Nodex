package io.github.frostzie.nodex.services.config.project

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.frostzie.nodex.api.config.LayoutPersistence
import io.github.frostzie.nodex.domain.config.LayoutConfig
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path

/**
 * Service responsible for managing project-specific layout configurations (.nodex/layout.json).
 */
class LayoutConfigService(
    private val projectConfigService: ProjectConfigService,
    private val fileOps: FileOperations
) : LayoutPersistence {
    private val logger = LoggerProvider.getLogger("LayoutConfigService")
    private val mapper = ObjectMapper().registerKotlinModule()

    /**
     * Loads the layout configs for a given project.
     */
    override fun load(projectRoot: Path): LayoutConfig {
        val configPath = getConfigPath(projectRoot)

        if (!fileOps.exists(configPath)) {
            logger.debug("No layout config found at {}, using defaults.", configPath)
            return LayoutConfig()
        }

        return try {
            val json = fileOps.readText(configPath)
            mapper.readValue(json, LayoutConfig::class.java)
        } catch (e: Exception) {
            logger.error("Failed to load layout config for project: $projectRoot", e)
            LayoutConfig()
        }
    }

    /**
     * Saves the layout configs for a given project.
     */
    override fun save(projectRoot: Path, config: LayoutConfig) {
        if (projectConfigService.lockLost) {
            logger.warn("Project lock was lost for $projectRoot. Skipping layout save to prevent data corruption.")
            return
        }

        val configPath = getConfigPath(projectRoot)
        try {
            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config)
            fileOps.writeAtomic(configPath, json)
            logger.debug("Successfully saved layout config for project: {}", projectRoot)
        } catch (e: Exception) {
            logger.error("Failed to save layout config for project: $projectRoot", e)
        }
    }

    private fun getConfigPath(projectRoot: Path): Path =
        projectRoot.resolve(".nodex").resolve("layout.json")
}
