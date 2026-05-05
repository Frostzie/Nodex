package io.github.frostzie.nodex.services.core

import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import javafx.animation.AnimationTimer
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Service that tracks FPS and Memory usage.
 */
class PerformanceService : PerformanceMonitor {
    private val _fpsProperty = SimpleStringProperty("FPS: 0")
    override val fpsProperty: ReadOnlyStringProperty = _fpsProperty

    private val _memoryProperty = SimpleStringProperty("Mem: 0 MB")
    override val memoryProperty: ReadOnlyStringProperty = _memoryProperty

    private var timer: AnimationTimer? = null

    override fun initialize() {
        start()
    }

    private fun start() {
        timer = object : AnimationTimer() {
            private var lastTime: Long = 0
            private var frameCount = 0

            override fun handle(now: Long) {
                if (lastTime == 0L) {
                    lastTime = now
                    return
                }

                frameCount++
                val elapsed = now - lastTime

                // Update every 0.5 seconds
                if (elapsed >= 500_000_000) {
                    val fps = frameCount * 1_000_000_000.0 / elapsed

                    // Memory (Used = Total - Free)
                    val runTime = Runtime.getRuntime()
                    val usedMemory = (runTime.totalMemory() - runTime.freeMemory()) / 1024 / 1024

                    _fpsProperty.set(String.format("FPS: %.0f", fps))
                    _memoryProperty.set("Mem: $usedMemory MB")

                    lastTime = now
                    frameCount = 0
                }
            }
        }
        timer?.start()
    }
}
