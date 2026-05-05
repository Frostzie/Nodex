package io.github.frostzie.nodex.ui.builder

import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import javafx.scene.layout.Region

/**
 * Builds overlay content for a specific [OverlayScreen].
 */
interface OverlayBuilder {
    val screen: OverlayScreen
    fun build(): Region
}
