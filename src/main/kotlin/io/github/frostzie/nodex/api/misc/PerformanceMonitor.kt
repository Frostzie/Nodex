package io.github.frostzie.nodex.api.misc

import javafx.beans.property.ReadOnlyStringProperty

/**
 * Tracks FPS and memory usage.
 *
 * @see io.github.frostzie.nodex.services.core.PerformanceService
 */
interface PerformanceMonitor {
    /** FPS string property (e.g. "FPS: 60"). */
    val fpsProperty: ReadOnlyStringProperty

    /** Memory string property (e.g. "Mem: 256 MB"). */
    val memoryProperty: ReadOnlyStringProperty

    /** Starts the monitoring timer. */
    fun initialize()
}
