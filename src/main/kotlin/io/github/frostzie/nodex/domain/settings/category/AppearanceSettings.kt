package io.github.frostzie.nodex.domain.settings.category

data class AppearanceSettings(
    val theme: ThemeOption = ThemeOption.PRIMER_DARK,
    val fontSize: Int = 14
)

enum class ThemeOption(val displayName: String) {
    PRIMER_LIGHT("Primer Light"),
    PRIMER_DARK("Primer Dark"),
    NORD_LIGHT("Nord Light"),
    NORD_DARK("Nord Dark"),
    CUPERTINO_LIGHT("Cupertino Light"),
    CUPERTINO_DARK("Cupertino Dark"),
    DRACULA("Dracula")
}
