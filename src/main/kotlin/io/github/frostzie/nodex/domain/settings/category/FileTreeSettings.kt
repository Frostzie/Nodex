package io.github.frostzie.nodex.domain.settings.category

data class FileTreeSettings (
    val showFolderIcon: Boolean = true,
    val showFileIcon: Boolean = true,
    val emptyFolderSeparator: FolderSeparator = FolderSeparator.DOT
)

enum class FolderSeparator {
    SLASH,
    DOT
}
