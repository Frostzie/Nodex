package io.github.frostzie.nodex.api.misc

/**
 * Manages apps stylesheet.
 *
 * TODO: Remake just temp to get styles loaded.
 *
 * @see io.github.frostzie.nodex.services.ui.StylingService
 */
interface Styling {
    fun getStylesheetUrls(): List<String>
    fun registerCss(id: String, classpathPath: String)
}
