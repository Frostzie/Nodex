package io.github.frostzie.nodex.services.core

import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.utils.LoggerProvider
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Low-level file I/O operations with watcher support.
 *
 * This service acts as a wrapper around [Files], ensuring that file modifications
 * are correctly synchronized with the [FileWatcher] to prevent internal changes from
 * triggering external refreshes.
 *
 * All operations in this service should be executed on an appropriate I/O thread.
 */
class FileService(private val fileWatcher: FileWatcher) : FileOperations {
    private val logger = LoggerProvider.getLogger("FileService")

    /**
     * Reads the content of a text file.
     *
     * @param path The path to the file.
     * @return The file content as a String.
     * @throws Exception if reading fails.
     */
    override fun readText(path: Path): String {
        try {
            return path.readText()
        } catch (e: Exception) {
            logger.error("Failed to read text from file: $path", e)
            throw e
        }
    }

    /**
     * Writes content to a text file.
     *
     * @param path The path to the file.
     * @param content The string content to write.
     * @throws Exception if writing fails.
     */
    override fun writeText(path: Path, content: String) {
        try {
            fileWatcher.ignorePath(path)
            path.writeText(content)
        } catch (e: Exception) {
            logger.error("Failed to write text to file: $path", e)
            throw e
        }
    }

    /**
     * Writes content to a file atomically using a temp file.
     *
     * Ensures that even if the application or system crashes during the writing,
     * the original file remains intact.
     *
     * @param path The path to the file.
     * @param content The string content to write.
     * @throws Exception if writing fails.
     */
    override fun writeAtomic(path: Path, content: String) {
        val parent = path.parent
            ?: throw IllegalArgumentException("Atomic write requires a parent directory: $path")

        if (!Files.exists(parent)) {
            createDirectory(parent)
        }

        var tempFile: Path? = null
        try {
            val currentTempFile = Files.createTempFile(parent, "${path.fileName}.", ".tmp")
            tempFile = currentTempFile
            fileWatcher.ignorePath(currentTempFile)
            currentTempFile.writeText(content)
            move(currentTempFile, path)
        } catch (e: Exception) {
            logger.error("Failed to write atomic to file: $path", e)
            try {
                tempFile?.let { Files.deleteIfExists(it) }
            } catch (_: Exception) {
            }
            throw e
        }
    }

    /**
     * Moves a file or directory from source to target.
     * Tries atomic move first, falls back to standard move.
     * 
     * Telling the FileWatcherService to ignore these paths during the move
     * to prevent internal changes from triggering external refresh events.
     *
     * @param source The source path.
     * @param target The target path.
     * @throws Exception if the move fails.
     */
    override fun move(source: Path, target: Path) {
        try {
            fileWatcher.ignorePath(source)
            fileWatcher.ignorePath(target)
            try {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                logger.warn("Atomic move not supported, falling back to standard move for: $source -> $target")
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
            logger.debug("Moved path: {} -> {}", source, target)
        } catch (e: Exception) {
            logger.error("Failed to move path from $source to $target", e)
            throw e
        }
    }

    /**
     * Copies a file or directory recursively.
     */
    override fun copy(source: Path, target: Path) {
        try {
            if (Files.isDirectory(source)) {
                copyDirectory(source, target)
            } else {
                fileWatcher.ignorePath(target)
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
            logger.debug("Copied path: {} -> {}", source, target)
        } catch (e: Exception) {
            logger.error("Failed to copy from $source to $target", e)
            throw e
        }
    }

    private fun copyDirectory(source: Path, target: Path) {
        createDirectory(target)
        source.listDirectoryEntries().forEach { file ->
            val destination = target.resolve(file.fileName)
            copy(file, destination)
        }
    }

    /**
     * Deletes a file or directory (recursively).
     * @throws Exception if deletion fails.
     */
    override fun delete(path: Path) {
        try {
            if (!Files.exists(path)) return

            fileWatcher.ignorePath(path)
            if (Files.isDirectory(path)) {
                Files.walk(path).sorted(Comparator.reverseOrder()).forEach { p ->
                    fileWatcher.ignorePath(p)
                    Files.delete(p)
                }
            } else {
                Files.delete(path)
            }
            logger.debug("Deleted path: {}", path)
        } catch (e: Exception) {
            logger.error("Failed to delete path: $path", e)
            throw e
        }
    }

    /**
     * Creates a directory at the specified path.
     *
     * @param path The directory path to create.
     * @throws Exception if creation fails.
     */
    override fun createDirectory(path: Path) {
        try {
            if (Files.exists(path)) return
            fileWatcher.ignorePath(path)
            Files.createDirectories(path)
            logger.debug("Created directory: {}", path)
        } catch (e: Exception) {
            logger.error("Failed to create directory: $path", e)
            throw e
        }
    }

    /**
     * Checks if a path exists.
     */
    override fun exists(path: Path): Boolean = Files.exists(path)

    /**
     * Checks if a directory is empty.
     */
    override fun isDirectoryEmpty(path: Path): Boolean {
        return try {
            Files.list(path).use { it.findFirst().isEmpty }
        } catch (e: Exception) {
            logger.warn("Failed to check if directory is empty: $path", e)
            false
        }
    }

    /**
     * Lists directory entries.
     *
     * @param path The directory path.
     * @return List of paths in the directory, or empty list if failed or not a directory.
     */
    override fun listDirectory(path: Path): List<Path> {
        return try {
            path.listDirectoryEntries()
        } catch (e: Exception) {
            logger.error("Failed to list directory entries: $path", e)
            emptyList()
        }
    }

    /**
     * Checks if a path is a directory.
     *
     * @param path The path to check.
     * @return True if it is a directory, false otherwise.
     */
    override fun isDirectory(path: Path): Boolean {
        return try {
            path.isDirectory()
        } catch (_: Exception) {
            false
        }
    }
}
