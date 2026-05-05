package io.github.frostzie.nodex.domain.tree

import java.nio.file.Path

data class FileTreeState(
    val rootPath: Path,
    val nodes: Map<String, FileNodeState>,
    val rootIds: List<String>
)
