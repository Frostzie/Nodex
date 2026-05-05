package io.github.frostzie.nodex.ui.utils

import io.github.frostzie.nodex.loader.minecraft.MCInterface
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.scene.control.Hyperlink
import java.net.URI

object HyperlinkUtils {

    private val logger = LoggerProvider.getLogger("HyperlinkUtils")

    fun create(text: String, url: String): Hyperlink {
        return create(text) {
            if (url.isBlank()) {
                logger.warn("Attempted to create a hyperlink with a blank URL.")
                return@create
            }

            try {
                val uri = URI(url)
                if (uri.scheme == null) {
                    logger.error("Failed to open URI: '$url' is not an absolute URI (missing scheme).")
                    return@create
                }

                try {
                    MCInterface.openUri(uri)
                } catch (e: Exception) {
                    logger.error("Failed to open URI: $url", e)
                }
            } catch (e: Exception) {
                logger.error("Malformed URL: $url", e)
            }
        }
    }

    fun create(text: String, action: () -> Unit): Hyperlink {
        return Hyperlink(text).apply {
            setOnAction { action() }
            isFocusTraversable = false
            visitedProperty().addListener { _, _, isVisited ->
                if (isVisited) this.isVisited = false
            }
        }
    }
}