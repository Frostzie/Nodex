package io.github.frostzie.nodex.styling.css

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Registry for scene-level CSS stylesheets.
 * Manages internal component styles, user overrides, and plugin changes.
 */
object StyleRegistry {
    private val stylesheets = CopyOnWriteArrayList<StyleSheet>()

    init {
        registerBuiltInCss()
    }

    private fun registerBuiltInCss() {
        val cssFiles = "/assets/nodex/styling/components/"

        register(
            StyleSheet(
                id = "code-area",
                sourceUrl = cssFiles + "code-area.css",
                source = StyleSource.INTERNAL,
                priority = 1
            )
        )
        register(
            StyleSheet(
                id = "menu-bar",
                sourceUrl = cssFiles + "menu-bar.css",
                source = StyleSource.INTERNAL,
                priority = 1
            )
        )
    }

    /**
     * Registers a stylesheet.
     * Stylesheets are applied to every managed Scene.
     */
    fun register(sheet: StyleSheet) {
        // If ID exists, remove it first (Replace behavior)
        stylesheets.removeIf { it.id == sheet.id }
        stylesheets.add(sheet)

        // Ensure deterministic order: Internal -> Plugin -> User
        stylesheets.sort()
    }

    /**
     * Unregisters a stylesheet by ID.
     */
    fun unregister(id: String) {
        stylesheets.removeIf { it.id == id }
    }

    /**
     * Returns all registered stylesheets, sorted by priority.
     */
    fun getStylesheets(): List<StyleSheet> {
        return stylesheets.toList()
    }
}
