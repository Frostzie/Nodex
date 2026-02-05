package io.github.frostzie.nodex.ui.util

import javafx.animation.AnimationTimer
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Tracks fps of JavaFX and memory usage of the jvm instance.
 */
class PerformanceTracker {
    private val _fpsProperty = SimpleStringProperty("FPS: 0")
    val fpsProperty: ReadOnlyStringProperty = _fpsProperty

    private val _memoryProperty = SimpleStringProperty("Mem: 0 MB")
    val memoryProperty: ReadOnlyStringProperty = _memoryProperty

    fun start() {
        object : AnimationTimer() {
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

                    //TODO: get cpu usage

                    _fpsProperty.set(String.format("FPS: %.0f", fps))
                    _memoryProperty.set("Mem: $usedMemory MB")

                    lastTime = now
                    frameCount = 0
                }
            }
        }.start()
    }
}