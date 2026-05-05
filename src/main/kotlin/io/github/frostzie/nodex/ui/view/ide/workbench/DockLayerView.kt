package io.github.frostzie.nodex.ui.view.ide.workbench

import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.ui.view.ide.workbench.editor.EditorAreaView
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.DockLayerViewModel
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.input.TransferMode
import javafx.scene.layout.*

/**
 * The DockLayer area containing the Tool Windows.
 * Handles the base horizontal split (LEFT/EDITOR/RIGHT).
 */
class DockLayerView(
    private val viewModel: DockLayerViewModel,
    private val editorAreaView: EditorAreaView,
    private val toolViews: Map<ToolWindow, Node>
) : StackPane() {
    private val logger = LoggerProvider.getLogger("DockLayerView")
    private var isInternalUpdate = false
    private var rebuildScheduled = false

    private val innerSplit = SplitPane().apply { orientation = Orientation.HORIZONTAL }

    // Maps to track which Tool owns which divider
    private val innerDividerMap = mutableMapOf<Int, ToolWindow>()

    init {
        // Fixes the tool windows from expanding* out of view when stage is squished.
        minWidth = 0.0
        innerSplit.minWidth = 0.0

        children.add(innerSplit)

        setupLayout()
        setupDragAndDrop()
    }

    private fun setupLayout() {
        viewModel.toolWindowStates.addListener(ListChangeListener { change ->
            if (isInternalUpdate) return@ListChangeListener
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                    scheduleRebuild()
                    break
                }
            }
        })

        rebuildWorkbench()
    }

    private fun scheduleRebuild() {
        if (rebuildScheduled) return
        rebuildScheduled = true
        Platform.runLater {
            rebuildScheduled = false
            rebuildWorkbench()
        }
    }

    private fun rebuildWorkbench() {
        logger.debug("Rebuilding DockLayer structure")
        innerSplit.items.clear()
        innerDividerMap.clear()

        val activeTools = viewModel.toolWindowStates.filter { it.visible }

        // Horizontal: LEFT, EDITOR, RIGHT
        val leftTool = activeTools.find { it.anchor == PanelPosition.LEFT }
        val rightTool = activeTools.find { it.anchor == PanelPosition.RIGHT }

        if (leftTool != null) {
            toolViews[leftTool.toolType]?.let { innerSplit.items.add(it) }
            innerDividerMap[0] = leftTool.toolType
        }

        innerSplit.items.add(editorAreaView)

        if (rightTool != null) {
            val dividerIdx = innerSplit.items.size - 1
            toolViews[rightTool.toolType]?.let { innerSplit.items.add(it) }
            innerDividerMap[dividerIdx] = rightTool.toolType
        }

        innerSplit.items.forEach { SplitPane.setResizableWithParent(it, true) }


        Platform.runLater {
            applyDividerPositions()
            attachDividerListeners()
        }
    }

    private fun attachDividerListeners() {
        innerSplit.dividers.forEachIndexed { idx, d ->
            d.positionProperty().addListener { _, _, newPos ->
                innerDividerMap[idx]?.let { updateSizeFromDivider(it, newPos.toDouble()) }
            }
        }
    }

    private fun updateSizeFromDivider(toolType: ToolWindow, pos: Double) {
        if (isInternalUpdate) return
        isInternalUpdate = true
        try {
            viewModel.onDividerMoved(toolType, pos)
        } finally {
            isInternalUpdate = false
        }
    }

    private fun applyDividerPositions() {
        isInternalUpdate = true
        try {
            innerDividerMap.forEach { (idx, toolType) ->
                if (idx < innerSplit.dividers.size) {
                    innerSplit.setDividerPosition(idx, viewModel.getEffectiveDividerPosition(toolType))
                }
            }
        } finally {
            isInternalUpdate = false
        }
    }

    private fun setupDragAndDrop() {
        setOnDragOver { event ->
            val toolTypeName = event.dragboard.string ?: ""
            if (ToolWindow.entries.any { it.name == toolTypeName }) {
                viewModel.onDragOver(event.x, width)
                if (viewModel.currentDropTarget.get() != null) {
                    event.acceptTransferModes(TransferMode.MOVE)
                }
            }
            event.consume()
        }

        setOnDragExited { viewModel.onDragExited() }

        setOnDragDropped { event ->
            val toolTypeName = event.dragboard.string ?: ""
            val toolType = ToolWindow.entries.find { it.name == toolTypeName }
            var success = false
            if (toolType != null) {
                val newPos = viewModel.calculateDropPosition(event.x, width)
                if (newPos != null) {
                    viewModel.onPanelDropped(toolType, newPos)
                    success = true
                }
            }
            viewModel.onDragExited()
            event.isDropCompleted = success
            event.consume()
        }
    }
}
