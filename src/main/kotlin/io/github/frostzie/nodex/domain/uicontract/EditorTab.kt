package io.github.frostzie.nodex.domain.uicontract

import java.nio.file.Path

data class EditorTab(
    val id: String,
    val path: Path,
    val fileName: String = path.fileName?.toString() ?: "Untitled",
    val content: String = "",
    val dirty: Boolean = false,
    val isActive: Boolean = false
)
