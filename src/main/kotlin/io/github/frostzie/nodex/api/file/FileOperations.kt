package io.github.frostzie.nodex.api.file

import java.nio.file.Path

/**
 * Low-level file I/O operations with watcher support.
 *
 * Provides file operations with watcher integration to prevent
 * internal changes from triggering external refresh events.
 *
 * @see io.github.frostzie.nodex.services.core.FileService
 */
interface FileOperations {
    /** Reads the content of a text file. */
    fun readText(path: Path): String

    /** Writes content to a text file. */
    fun writeText(path: Path, content: String)

    /** Writes content to a file atomically. */
    fun writeAtomic(path: Path, content: String)

    /** Moves a file or directory from source to target. */
    fun move(source: Path, target: Path)

    /** Copies a file or directory recursively. */
    fun copy(source: Path, target: Path)

    /** Deletes a file or directory (recursively). */
    fun delete(path: Path)

    /** Creates a directory at the specified path. */
    fun createDirectory(path: Path)

    /** Checks if a path exists. */
    fun exists(path: Path): Boolean

    /** Checks if a directory is empty. */
    fun isDirectoryEmpty(path: Path): Boolean

    /** Lists directory entries. */
    fun listDirectory(path: Path): List<Path>

    /** Checks if a path is a directory. */
    fun isDirectory(path: Path): Boolean
}
