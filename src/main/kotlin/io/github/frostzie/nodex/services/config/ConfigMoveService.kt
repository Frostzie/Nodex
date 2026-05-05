package io.github.frostzie.nodex.services.config

import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path

/**
 * Service responsible for migrating config data between locations.
 */
class ConfigMoveService(private val fileOps: FileOperations) {
    private val logger = LoggerProvider.getLogger("ConfigMoveService")

    /**
     * Moves config from [source] to [target].
     */
    fun moveConfig(source: Path, target: Path) {
        if (!fileOps.exists(source)) return

        if (target.toAbsolutePath().startsWith(source.toAbsolutePath())) {
            logger.error("Cannot move config: Target is inside source path ($source -> $target)")
            return
        }

        if (fileOps.exists(target) && !fileOps.isDirectoryEmpty(target)) {
            logger.warn("Config move skipped: Target directory is not empty ($target)")
            return
        }

        try {
            val targetParent = target.parent
            if (targetParent != null && !fileOps.exists(targetParent)) {
                fileOps.createDirectory(targetParent)
            }
            fileOps.move(source, target)
            logger.debug("Successfully moved config: {} -> {}", source, target)
        } catch (e: Exception) {
            logger.error("Failed to move config from $source to $target", e)
        }
    }
}
