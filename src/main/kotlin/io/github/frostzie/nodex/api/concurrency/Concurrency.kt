package io.github.frostzie.nodex.api.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Centralizes thread management and coroutine scopes for the app.
 *
 * Provides dispatchers for I/O, CPU, and UI operations, along with helpers
 * to run code on the appropriate thread.
 *
 * @see io.github.frostzie.nodex.services.core.ConcurrencyService
 */
interface Concurrency {
    /** Dispatcher for Input/Output operations (File reading, Network). */
    val ioDispatcher: CoroutineDispatcher

    /** Dispatcher for UI operations (Main = JavaFX App Thread). */
    val uiDispatcher: CoroutineDispatcher

    /** Dispatcher for CPU-intensive operations (Data parsing, calculations). */
    val cpuDispatcher: CoroutineDispatcher

    /** Global scope for IO operations that live as long as the app. */
    val ioScope: CoroutineScope

    /** Global scope for CPU operations. */
    val cpuScope: CoroutineScope

    /** Run a block on the UI thread safely. */
    fun runOnUI(action: () -> Unit)

    /** Run a block on the I/O thread pool asynchronously. */
    fun runOnIO(action: () -> Unit)

    /** Run a block on the CPU-intensive thread pool asynchronously. */
    fun runOnCPU(action: () -> Unit)

    /** Forces the action to run on the next UI pulse. */
    fun runLater(action: () -> Unit)

    /** Cancels all active coroutines. Call on application shutdown. */
    fun shutdown()
}
