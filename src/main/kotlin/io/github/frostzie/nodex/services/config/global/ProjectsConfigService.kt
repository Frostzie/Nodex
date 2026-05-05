package io.github.frostzie.nodex.services.config.global

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.frostzie.nodex.domain.entity.RecentProject
import io.github.frostzie.nodex.services.config.BackupService
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path

/**
 * Service responsible for reading the projects history (projects.json).
 * Supports silent reloads when external changes are detected.
 */
class ProjectsConfigService(
    private val projectsPath: Path,
    private val backupDir: Path,
    private val fileOps: FileOperations,
    private val fileWatcher: FileWatcher,
    private val backupService: BackupService
) {
    private val logger = LoggerProvider.getLogger("ProjectsConfigService")
    private val mapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    private var onReload: ((List<RecentProject>) -> Unit)? = null

    /**
     * Initializes the service by setting up a file watcher for silent reloads.
     */
    fun initialize(onReload: (List<RecentProject>) -> Unit) {
        this.onReload = onReload
        fileWatcher.watchFile(projectsPath) { _, _ ->
            logger.info("External change detected in projects history, reloading")
            try {
                val projects = load()
                onReload(projects)
            } catch (e: Exception) {
                logger.error("Failed to reload projects history after external change", e)
            }
        }
    }

    /**
     * Loads projects history from disk.
     */
    fun load(): List<RecentProject> {
        if (!fileOps.exists(projectsPath)) {
            return emptyList()
        }

        return try {
            val json = fileOps.readText(projectsPath)
            mapper.readValue(json, object : TypeReference<List<RecentProject>>() {})
        } catch (e: Exception) {
            logger.error("Failed to load projects history from: $projectsPath", e)
            emptyList()
        }
    }

    /**
     * Saves [projects] history to disk.
     */
    fun save(projects: List<RecentProject>) {
        try {
            fileWatcher.ignorePath(projectsPath)

            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(projects)
            fileOps.writeAtomic(projectsPath, json)
            logger.debug("Projects history saved.")

            backupService.backup(projectsPath, backupDir, 2)
        } catch (e: Exception) {
            logger.error("Failed to save projects history to: $projectsPath", e)
            throw e
        }
    }
}
