package io.github.frostzie.nodex.domain.tree

import java.nio.file.Path

/**
 * Represents a specific change to the file system that should be reflected in the UI tree.
 */
sealed class FileTreeChange {
    /**
     * A file's content was modified.
     */
    data class FileModified(val path: Path) : FileTreeChange()

    /**
     * A new file or folder was created.
     */
    data class FileCreated(val path: Path) : FileTreeChange()

    /**
     * A file or folder was deleted.
     */
    data class FileDeleted(val path: Path) : FileTreeChange()

    /**
     * A folder's content is no longer reliable and needs a targeted rescan.
     */
    data class ParentInvalidated(val parentPath: Path, val reason: String) : FileTreeChange()

    /**
     * A full rescan of the project file system is required.
     */
    data object FileSystemRescanRequired : FileTreeChange()
}
