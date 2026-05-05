package io.github.frostzie.nodex.api.config

import io.github.frostzie.nodex.domain.config.LayoutConfig
import java.nio.file.Path

/**
 * Manages project-specific layout configurations (.nodex/layout.json).
 *
 * @see io.github.frostzie.nodex.services.config.project.LayoutConfigService
 */
interface LayoutPersistence {

    /**
     * Loads layout config for a given [projectRoot], returning defaults if missing.
     */
    fun load(projectRoot: Path): LayoutConfig

    /**
     * Saves [config] to [projectRoot]'s layout.json.
     */
    fun save(projectRoot: Path, config: LayoutConfig)
}
