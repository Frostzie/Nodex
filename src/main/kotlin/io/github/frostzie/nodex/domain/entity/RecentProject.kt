package io.github.frostzie.nodex.domain.entity

import java.nio.file.Path
import java.time.Instant

/**
 * Represents a project that was recently opened in the IDE.
 * 
 * Used for project history and session restoration.
 */
data class RecentProject(
    val path: Path,
    val lastOpened: Instant = Instant.now(),
    val active: Boolean = false
)
