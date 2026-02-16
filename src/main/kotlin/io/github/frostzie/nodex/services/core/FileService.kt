package io.github.frostzie.nodex.services.core

import io.github.frostzie.nodex.services.files.FileWatcherService
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
 * are correctly synchronized with the [FileWatcherService] to prevent internal changes from
 * triggering external refreshes.
 *
 * All operations in this service should be executed on an appropriate I/O thread.
 */
class FileService(private val fileWatcherService: FileWatcherService) {
    private val logger = LoggerProvider.getLogger("FileService")

    /**
     * Reads the content of a text file.
     *
     * @param path The path to the file.
     * @return The file content as a String.
     * @throws Exception if reading fails.
     */
    fun readText(path: Path): String {
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
    fun writeText(path: Path, content: String) {
        try {
            fileWatcherService.ignorePath(path)
            path.writeText(content)
        } catch (e: Exception) {
            logger.error("Failed to write text to file: $path", e)
            throw e
        }
    }

    /**
     * Moves a file or directory from source to target.
     * Tries atomic move first, falls back to standard copy-delete.
     *
     * @param source The source path.
     * @param target The target path.
     * @throws Exception if the move fails.
     */
    fun move(source: Path, target: Path) {
        try {
            try {
                fileWatcherService.ignorePath(source)
                fileWatcherService.ignorePath(target)
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                logger.warn("Atomic move not supported, falling back to standard move for: $source -> $target")
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
            logger.debug("Moved file: {} -> {}", source, target)
        } catch (e: Exception) {
            logger.error("Failed to move file from $source to $target", e)
            throw e
        }
    }

    /**
     * Deletes a file or directory (recursively not guaranteed here, standard delete). //TODO: Improve
     * @throws Exception if deletion fails.
     */
    fun delete(path: Path) {
        try {
            fileWatcherService.ignorePath(path)
            Files.delete(path)
            logger.debug("Deleted file: {}", path)
        } catch (e: Exception) {
            logger.error("Failed to delete file: $path", e)
            throw e
        }
    }
    
    /**
     * Creates a directory at the specified path.
     *
     * @param path The directory path to create.
     * @throws Exception if creation fails.
     */
    fun createDirectory(path: Path) {
        try {
            fileWatcherService.ignorePath(path)
            Files.createDirectories(path)
            logger.debug("Created directory: {}", path)
        } catch (e: Exception) {
            logger.error("Failed to create directory: $path", e)
            throw e
        }
    }

    /**
     * Lists directory entries.
     *
     * @param path The directory path.
     * @return List of paths in the directory, or empty list if failed or not a directory.
     */
    fun listDirectory(path: Path): List<Path> {
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
    fun isDirectory(path: Path): Boolean {
        return try {
            path.isDirectory()
        } catch (_: Exception) {
            false
        }
    }
}
