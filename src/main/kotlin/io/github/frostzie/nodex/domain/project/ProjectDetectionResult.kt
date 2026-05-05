package io.github.frostzie.nodex.domain.project

import java.nio.file.Path

/**
 * Represents the pack metadata from pack.mcmeta.
 */
data class PackMcmeta(
    val packFormat: Int? = null,
    val supportedFormats: List<Int>? = null,
    val minFormat: Int? = null,
    val maxFormat: Int? = null,
    val description: String? = null
)

/**
 * Represents the result of project detection at a given root.
 */
data class ProjectDetectionResult(
    val root: Path,
    val projectType: ProjectType,
    val packTypes: Set<PackType>,
    val mcmeta: PackMcmeta? = null
)
