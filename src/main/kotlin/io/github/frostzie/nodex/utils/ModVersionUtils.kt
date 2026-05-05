package io.github.frostzie.nodex.utils

/**
 * Utility for comparing semantic* version strings (Major.Minor.Patch/Alpha).
 *
 * *Not fully semantic versioning to clarify alpha and patch versions are considered the same.*
 */
object ModVersionUtils {

    /**
     * Parses a version string into an ordered list of integer segments.
     */
    private fun segments(version: String): List<Int> =
        version.trim().split(".").map { it.toIntOrNull() ?: 0 }

    /**
     * Compares two version strings segment by segment.
     *
     * Returns:
     *  - negative if [a] < [b]
     *  - zero     if [a] == [b]
     *  - positive if [a] > [b]
     */
    fun compare(a: String, b: String): Int {
        val segA = segments(a)
        val segB = segments(b)
        val len  = maxOf(segA.size, segB.size)

        for (i in 0 until len) {
            val v1 = segA.getOrElse(i) { 0 }
            val v2 = segB.getOrElse(i) { 0 }
            if (v1 != v2) return v1.compareTo(v2)
        }

        return 0
    }

    fun isOlderThan(a: String, b: String): Boolean = compare(a, b) < 0
    fun isNewerThan(a: String, b: String): Boolean = compare(a, b) > 0
    fun isSameVersion(a: String, b: String): Boolean = compare(a, b) == 0
}
