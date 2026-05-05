package io.github.frostzie.nodex.domain.tree

import java.nio.file.Path

data class FileNodeState(
    val id: String,
    val path: Path,
    val name: String,
    val isDirectory: Boolean,
    val childIds: List<String>
)
