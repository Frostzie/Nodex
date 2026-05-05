package io.github.frostzie.nodex.domain.entity

/**
 * Holds the current mod version.
 * Written once on startup, then read only.
 */
object ModInfo {
    var version: String = "unknown"
        internal set
}