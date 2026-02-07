package io.github.frostzie.nodex.styling.css

import javafx.scene.Scene

/**
 * Stateless utility to apply registered stylesheets to a JavaFX Scene.
 * Should be called whenever a new Scene is created.
 */
object SceneStyler {

    /**
     * Applies all registered [StyleSheet]s to the provided [Scene].
     */
    fun apply(scene: Scene) {
        val sheets = StyleRegistry.getStylesheets()
        val sceneStyles = scene.stylesheets

        sheets.forEach { sheet ->
            //TODO: allow replacing sheets
            if (sheet.sourceUrl.isNotEmpty() && !sceneStyles.contains(sheet.sourceUrl)) {
                sceneStyles.add(sheet.sourceUrl)
            }
        }
    }
}