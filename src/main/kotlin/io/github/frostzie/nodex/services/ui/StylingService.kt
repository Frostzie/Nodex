package io.github.frostzie.nodex.services.ui

import atlantafx.base.theme.PrimerDark
import io.github.frostzie.nodex.api.misc.Styling
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.application.Application
import java.util.concurrent.ConcurrentHashMap

//TODO: Remake just temp to get styles loaded.
class StylingService : Styling {
    private val logger = LoggerProvider.getLogger("StylingService")
    private val stylesheets = ConcurrentHashMap<String, String>()

    init {
        Application.setUserAgentStylesheet(PrimerDark().userAgentStylesheet)
        registerBuiltInCss()
    }

    override fun getStylesheetUrls(): List<String> = stylesheets.values.toList()

    override fun registerCss(id: String, classpathPath: String) {
        StylingService::class.java
            .getResource(classpathPath)
            ?.toExternalForm()
            ?.let { stylesheets[id] = it }
            ?: logger.warn("CSS resource not found: $classpathPath")
    }

    private fun registerBuiltInCss() {
        val base = "/assets/nodex/styling/components"
        registerCss("code-area", "$base/controls/code-area.css")
        registerCss("menu-button", "$base/controls/menu-button.css")
        registerCss("tab", "$base/controls/tab-pane.css")
        registerCss("rectangle", "$base/primitives/rectangle.css")
        registerCss("settings", "$base/views/settings.css")
        registerCss("ide", "$base/views/ide.css")
    }
}
