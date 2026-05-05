package io.github.frostzie.nodex.services.config

import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Service for creating and managing rotating file backups.
 *
 * It ensures that a file is backed up with a unique timestamped name
 * and maintains a maximum number of historical backups.
 */
class BackupService(
    private val fileOps: FileOperations,
    private val timeSource: () -> Instant = { Instant.now() }
) {
    private val logger = LoggerProvider.getLogger("BackupService")
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss").withZone(ZoneOffset.UTC)

    /**
     * Backs up the [source] file to [backupDir].
     *
     * The backup filename format is: "{original_name}.{timestamp}"
     * Oldest backups are pruned if they exceed [maxCount].
     *
     * @param source The file to back up.
     * @param backupDir The directory where backups should be stored.
     * @param maxCount The maximum number of backups to keep for this file.
     */
    fun backup(source: Path, backupDir: Path, maxCount: Int) {
        if (!fileOps.exists(source)) return
        if (maxCount <= 0) return

        try {
            if (!fileOps.exists(backupDir)) {
                fileOps.createDirectory(backupDir)
            }

            val timestamp = formatter.format(timeSource())
            val originalName = source.fileName.toString()
            val backupName = "$originalName.$timestamp"
            val target = backupDir.resolve(backupName)

            fileOps.copy(source, target)
            logger.debug("Created backup: {} -> {}", source.fileName, backupName)

            removeOldBackups(backupDir, originalName, maxCount)
        } catch (e: Exception) {
            logger.error("Failed to create backup for: $source", e)
        }
    }

    private fun removeOldBackups(backupDir: Path, prefix: String, maxCount: Int) {
        try {
            val allBackups = fileOps.listDirectory(backupDir)
                .filter { it.fileName.toString().startsWith("$prefix.") }
                .sortedBy { it.fileName.toString() }

            if (allBackups.size > maxCount) {
                val toDelete = allBackups.take(allBackups.size - maxCount)
                toDelete.forEach { path ->
                    fileOps.delete(path)
                    logger.debug("Removed old backup: {}", path.fileName)
                }
            }
        } catch (e: Exception) {
            logger.warn("Failed to remove old backups in: $backupDir", e)
        }
    }
}