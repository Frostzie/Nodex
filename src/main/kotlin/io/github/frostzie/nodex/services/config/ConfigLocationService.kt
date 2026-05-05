package io.github.frostzie.nodex.services.config

import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.Path

/**
 * Service responsible for resolving the effective config folder.
 * Handles switching between local and universal config paths. (Moves only Tier 2 configs)
 */
class ConfigLocationService(
    private val fileOps: FileOperations,
    private val configService: Config,
    private val configMoveService: ConfigMoveService
) {
    private val logger = LoggerProvider.getLogger("ConfigLocationService")

    fun resolveEffectiveNodexDir(localNodexDir: Path): Path {
        if (!configService.universalPathEnabled || configService.universalPath.isNullOrBlank()) {
            return localNodexDir
        }

        val universalBase = Path.of(configService.universalPath!!)
        val universalNodexDir = universalBase.resolve("Nodex").resolve("config")

        // Moving only Tier 2 configs from local to universal
        val tier2Files = listOf("settings.json", "projects.json", "backups")

        for (name in tier2Files) {
            val src = localNodexDir.resolve(name)
            val dest = universalNodexDir.resolve(name)

            if (fileOps.exists(src)) {
                logger.info("Migrating config '$name' to universal path: $dest")
                try {
                    configMoveService.moveConfig(src, dest)
                } catch (e: Exception) {
                    logger.error(
                        "Failed to migrate config '$name' to universal path.\n Falling back to local config directory.",
                        e
                    )
                    return localNodexDir
                }
            }
        }

        return universalNodexDir
    }
}
