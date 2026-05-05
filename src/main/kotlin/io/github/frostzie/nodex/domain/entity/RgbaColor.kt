package io.github.frostzie.nodex.domain.entity

/**
 * Immutable domain representation of an RGBA color.
 *
 * Stored in JSON as `{ "r": Int, "g": Int, "b": Int, "a": Double }`.
 * - `r`, `g`, `b`: 0–255
 * - `a`: 0.0–1.0 (matches JavaFX Color.opacity)
 */
data class RgbaColor(
    val r: Int = 58,
    val g: Int = 130,
    val b: Int = 246,
    val a: Double = 1.0
) {
    init {
        require(r in 0..255) { "r must be 0-255, was $r" }
        require(g in 0..255) { "g must be 0-255, was $g" }
        require(b in 0..255) { "b must be 0-255, was $b" }
        require(a in 0.0..1.0) { "a must be 0.0-1.0, was $a" }
    }
}