package io.github.frostzie.nodex.utils.file

import io.github.frostzie.nodex.utils.LoggerProvider
import io.methvin.watcher.DirectoryChangeEvent.EventType
import io.methvin.watcher.DirectoryWatcher
import java.nio.file.Path
import kotlin.concurrent.thread

/**
 * A thin bridge around the [DirectoryWatcher] library.
 * Utility that provides raw observation of disk changes.
 *
 * @param watchPath The path to watch.
 * @param onAction The callback that is invoked when a disk change is detected.
 * This callback is executed on the [watchThread].
 */
class FileSystemWatcher(
    private val watchPath: Path,
    private val onAction: (Path, EventType) -> Unit
) {
    private val logger = LoggerProvider.getLogger("FileSystemWatcher")
    private var watcher: DirectoryWatcher? = null
    private var watchThread: Thread? = null

    fun start() {
        stop()

        try {
            watcher = DirectoryWatcher.builder()
                .path(watchPath)
                .listener { event ->
                    logger.debug("File system event: {} - {}", event.eventType(), event.path())
                    onAction(event.path(), event.eventType())
                }
                .build()

            watchThread = thread(name = "FileSystemWatcher-${watchPath.fileName}") {
                logger.debug("Started watching directory: {}", watchPath)
                try {
                    watcher?.watch()
                } catch (e: Exception) {
                    if (watcher != null) {
                        logger.error("Watcher for $watchPath terminated unexpectedly", e)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to start watcher for $watchPath", e)
        }
    }

    fun stop() {
        watchThread?.interrupt()
        try {
            watchThread?.join(500)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        watchThread = null

        try {
            watcher?.close()
        } catch (e: Exception) {
            logger.warn("Failed to close watcher for $watchPath cleanly", e)
        }
        watcher = null
        logger.debug("Stopped watcher for {}", watchPath)
    }
}