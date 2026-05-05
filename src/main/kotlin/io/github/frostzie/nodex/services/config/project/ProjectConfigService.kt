package io.github.frostzie.nodex.services.config.project

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path
import java.time.Instant

/**
 * Service responsible for claiming and managing project-level locks.
 * It uses a `.nodex/project.json` file to store project metadata and the active instance details.
 */
class ProjectConfigService(
    private val fileOps: FileOperations,
    private val fileWatcher: FileWatcher
) {
    private val logger = LoggerProvider.getLogger("ProjectConfigService")
    private val mapper = ObjectMapper().registerKotlinModule()

    private val currentPid = ProcessHandle.current().pid()
    private var claimedProject: Path? = null

    var lockLost: Boolean = false
        private set

    sealed class ClaimResult {
        object Success : ClaimResult()
        data class Locked(val pid: Long, val claimedAt: String) : ClaimResult()
    }

    private data class ActiveInstance(
        val pid: Long,
        val claimedAt: String
    )

    private data class ProjectConfig(
        val projectName: String,
        val createdAt: String,
        val lastOpened: String,
        val activeInstance: ActiveInstance? = null
    )

    /**
     * Attempts to claim a project for the current process.
     * 
     * @param projectRoot The root directory of the project.
     * @return [ClaimResult.Success] if the project was claimed.
     *         [ClaimResult.Locked] if another active process owns the project.
     */
    fun tryClaimProject(projectRoot: Path): ClaimResult {
        val configPath = getConfigPath(projectRoot)
        val now = Instant.now().toString()

        val currentConfig = if (fileOps.exists(configPath)) {
            try {
                val json = fileOps.readText(configPath)
                mapper.readValue(json, ProjectConfig::class.java)
            } catch (e: Exception) {
                logger.error("Failed to read project config, creating new one.", e)
                createInitialConfig(projectRoot)
            }
        } else {
            createInitialConfig(projectRoot)
        }

        val instance = currentConfig.activeInstance
        if (instance != null) {
            if (instance.pid == currentPid) {
                startWatching(projectRoot)
                claimedProject = projectRoot
                return ClaimResult.Success
            }

            if (isProcessAlive(instance.pid)) {
                logger.warn("Project $projectRoot is already locked by PID: ${instance.pid}")
                return ClaimResult.Locked(instance.pid, instance.claimedAt)
            } else {
                logger.debug("Previous instance (PID: ${instance.pid}) is dead. Reclaiming silently.")
            }
        }

        return try {
            val updatedConfig = currentConfig.copy(
                lastOpened = now,
                activeInstance = ActiveInstance(currentPid, now)
            )
            writeConfig(projectRoot, updatedConfig)
            startWatching(projectRoot)
            claimedProject = projectRoot
            lockLost = false
            ClaimResult.Success
        } catch (e: Exception) {
            logger.error("Failed to claim project $projectRoot", e)
            ClaimResult.Locked(-1, "unknown")
        }
    }

    private fun createInitialConfig(projectRoot: Path): ProjectConfig {
        val now = Instant.now().toString()
        return ProjectConfig(
            projectName = projectRoot.fileName.toString(),
            createdAt = now,
            lastOpened = now
        )
    }

    private fun writeConfig(projectRoot: Path, config: ProjectConfig) {
        val configPath = getConfigPath(projectRoot)
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config)

        fileWatcher.ignorePath(configPath)
        fileOps.writeAtomic(configPath, json)
    }

    private fun startWatching(projectRoot: Path) {
        val configPath = getConfigPath(projectRoot)
        fileWatcher.watchFile(configPath) { _, _ ->
            checkLockIntegrity(projectRoot)
        }
    }

    private fun checkLockIntegrity(projectRoot: Path) {
        val configPath = getConfigPath(projectRoot)
        try {
            if (!fileOps.exists(configPath)) {
                if (claimedProject == projectRoot) {
                    logger.warn("Config file for project $projectRoot was deleted externally.")
                    lockLost = true
                }
                return
            }

            val json = fileOps.readText(configPath)
            val config = mapper.readValue(json, ProjectConfig::class.java)
            if (config.activeInstance?.pid != currentPid) {
                logger.error("Lock for project $projectRoot was stolen or cleared by PID: ${config.activeInstance?.pid}.")
                lockLost = true
            }
        } catch (e: Exception) {
            logger.error("Failed to check lock integrity", e)
            lockLost = true
        }
    }

    private fun getConfigPath(projectRoot: Path): Path =
        projectRoot.resolve(".nodex").resolve("project.json")

    private fun isProcessAlive(pid: Long): Boolean =
        ProcessHandle.of(pid).filter { it.isAlive }.isPresent
}
