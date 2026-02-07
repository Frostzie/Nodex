package io.github.frostzie.nodex.styling.css

/**
 * Represents a single CSS stylesheet config to a Scene.
 *
 * @property id Unique identifier for this stylesheet.
 * @property sourceUrl The string URL to the CSS file.
 * @property source Origin of the style (Internal, User, Plugin).
 * @property priority Determines load order. Higher values load later and override lowers.
 */
data class StyleSheet(
    val id: String,
    val sourceUrl: String,
    val source: StyleSource,
    val priority: Int = 0
) : Comparable<StyleSheet> {

    override fun compareTo(other: StyleSheet): Int {
        // Sort by Source (Internal < Plugin < User), then by Priority
        val sourceComparison = this.source.ordinal.compareTo(other.source.ordinal)
        if (sourceComparison != 0) return sourceComparison
        
        return this.priority.compareTo(other.priority)
    }
}

/**
 * Defines the origin of a style module.
 * The ordinal order defines the default sort order (Internal first, User last).
 */
enum class StyleSource {
    INTERNAL,
    PLUGIN,
    USER
}
