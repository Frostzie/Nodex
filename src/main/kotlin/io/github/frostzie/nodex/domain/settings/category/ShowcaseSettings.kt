package io.github.frostzie.nodex.domain.settings.category

import io.github.frostzie.nodex.domain.entity.RgbaColor

data class ShowcaseSettings(
    val enabled: Boolean = true,
    val maxItems: Int = 25,
    val opacity: Double = 0.85,
    val displayName: String = "Showcase",
    val mode: ShowcaseMode = ShowcaseMode.BALANCED,
    val accentColor: RgbaColor = RgbaColor(),
    val theme: Int = 12,
    val fontSize: Int = 14,
    val testString: String = "test"
)

enum class ShowcaseMode(val displayName: String) {
    FAST("Fast"),
    BALANCED("Balanced"),
    PRECISE("Precise")
}
