package io.github.frostzie.nodex.ui.view.ide.overlay

import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle

/**
 * Overlay view that highlights the drop zones for Tool Windows.
 */
class ToolWindowDropOverlayView(
    dropTarget: ObservableValue<PanelPosition?>
) : Pane() {
    private val leftRect = createRect()
    private val rightRect = createRect()

    // Rn these have no use since only vertical bars are finished but eventually™
    private val topRect = createRect()
    private val bottomRect = createRect()

    init {
        isMouseTransparent = true
        children.addAll(leftRect, rightRect, topRect, bottomRect)

        widthProperty().addListener { _ -> updateShapes() }
        heightProperty().addListener { _ -> updateShapes() }

        dropTarget.addListener { _, _, pos ->
            updateHighlight(pos)
        }
    }

    private fun createRect(): Rectangle {
        return Rectangle().apply {
            styleClass.add("tool-window-drop-overlay")
            opacity = 0.0
        }
    }

    private fun updateShapes() {
        val w = width
        val h = height
        val thicknessW = w * 0.25
        val thicknessH = h * 0.25

        leftRect.x = 0.0
        leftRect.y = 0.0
        leftRect.width = thicknessW
        leftRect.height = h

        rightRect.x = w - thicknessW
        rightRect.y = 0.0
        rightRect.width = thicknessW
        rightRect.height = h

        topRect.x = thicknessW
        topRect.y = 0.0
        topRect.width = w - (thicknessW * 2)
        topRect.height = thicknessH

        bottomRect.x = thicknessW
        bottomRect.y = h - thicknessH
        bottomRect.width = w - (thicknessW * 2)
        bottomRect.height = thicknessH
    }

    private fun updateHighlight(pos: PanelPosition?) {
        topRect.opacity = 0.0
        bottomRect.opacity = 0.0
        leftRect.opacity = 0.0
        rightRect.opacity = 0.0

        // Ensure shapes are correct before highlighting
        updateShapes()

        if (pos != null) {
            when (pos) {
                PanelPosition.TOP -> {
                    topRect.opacity = 1.0
                    topRect.x = 0.0
                    topRect.width = width
                }

                PanelPosition.BOTTOM -> {
                    bottomRect.opacity = 1.0
                    bottomRect.x = 0.0
                    bottomRect.width = width
                }

                PanelPosition.LEFT -> leftRect.opacity = 1.0
                PanelPosition.RIGHT -> rightRect.opacity = 1.0
            }
        }
    }
}
