package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.domain.uicontract.DraggableWindowView
import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.ui.viewmodel.layout.IdeLayoutViewModel
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import kotlin.math.abs

/**
 * The Main Layout of the IDE.
 */
class IdeLayoutView: StackPane(), DraggableWindowView {

    private val viewModel = IdeLayoutViewModel()
    private val workbenchPane = SplitPane()

    // Draggable File Tree Placeholder
    private val fileTreePlaceholder: Pane = createFileTreePlaceholder()

    // Editor Placeholder
    private val editorPlaceholder = createDebugRegion("Editor Area", "#282c34")

    private val zoneOverlay = Pane().apply {
        isMouseTransparent = true
    }

    // Expose the Top Bar region for Window Dragging
    val topBar: Pane

    override val dragHandle: Node
        get() = topBar

    private var zoneHighlighter: ((PanelPosition?) -> Unit)? = null

    init {
        // The Static Shell
        val shell = BorderPane()

        // Top Area (TopBar)
        topBar = createDebugRegion("Top", "#e06c75", height = 35.0)
        shell.top = topBar

        // Left Area (LeftBar)
        shell.left = createDebugRegion("Left", "#98c379", width = 50.0)

        // Right Area (RightBar)
        shell.right = createDebugRegion("Right", "#61afef", width = 50.0)

        // Bottom Area (BottomBar)
        shell.bottom = createDebugRegion("Bottom", "#e5c07b", height = 25.0)

        // Center Area (FileTree + Editor)
        setupWorkbench()
        setupDragAndDrop()
        setupZoneOverlay()

        val workbenchStack = StackPane(workbenchPane, zoneOverlay)
        shell.center = workbenchStack

        children.addAll(shell)
    }

    private fun setupZoneOverlay() {
        val cssStyle = "-fx-fill: rgba(130, 200, 229, 0.4); -fx-stroke: rgba(130, 200, 229, 0.8);"

        val topRect = Rectangle()
        topRect.style = cssStyle
        topRect.opacity = 0.0

        val rightRect = Rectangle()
        rightRect.style = cssStyle
        rightRect.opacity = 0.0

        val bottomRect = Rectangle()
        bottomRect.style = cssStyle
        bottomRect.opacity = 0.0

        val leftRect = Rectangle()
        leftRect.style = cssStyle
        leftRect.opacity = 0.0

        zoneOverlay.children.addAll(topRect, rightRect, bottomRect, leftRect)

        // Update rectangle dimensions when size changes
        // Base state is H-Shape (Sides Priority)
        val updateShapes = {
            val w = zoneOverlay.width
            val h = zoneOverlay.height
            val thicknessW = w * 0.25
            val thicknessH = h * 0.25

            // Left Rect (Full Height)
            leftRect.x = 0.0
            leftRect.y = 0.0
            leftRect.width = thicknessW
            leftRect.height = h

            // Right Rect (Full Height)
            rightRect.x = w - thicknessW
            rightRect.y = 0.0
            rightRect.width = thicknessW
            rightRect.height = h

            // Top Rect (Middle Width)
            topRect.x = thicknessW
            topRect.y = 0.0
            topRect.width = w - (thicknessW * 2)
            topRect.height = thicknessH

            // Bottom Rect (Middle Width)
            bottomRect.x = thicknessW
            bottomRect.y = h - thicknessH
            bottomRect.width = w - (thicknessW * 2)
            bottomRect.height = thicknessH
        }

        zoneOverlay.widthProperty().addListener { _ -> updateShapes() }
        zoneOverlay.heightProperty().addListener { _ -> updateShapes() }

        // Use class property instead of userData to avoid unchecked cast
        zoneHighlighter = { pos ->
            topRect.opacity = 0.0
            bottomRect.opacity = 0.0
            leftRect.opacity = 0.0
            rightRect.opacity = 0.0

            // Reset dimensions to base state
            updateShapes()

            if (pos != null) {
                when (pos) {
                    // Expanding Top and Bottom bars to max width when dragging
                    PanelPosition.TOP -> {
                        topRect.opacity = 1.0
                        topRect.x = 0.0
                        topRect.width = zoneOverlay.width
                    }
                    PanelPosition.BOTTOM -> {
                        bottomRect.opacity = 1.0
                        bottomRect.x = 0.0
                        bottomRect.width = zoneOverlay.width
                    }
                    PanelPosition.LEFT -> leftRect.opacity = 1.0
                    PanelPosition.RIGHT -> rightRect.opacity = 1.0
                }
            }
        }
    }

    private fun createFileTreePlaceholder(): Pane {
        val root = VBox()

        // Header (Draggable)
        val header = VBox().apply {
            style = "-fx-background-color: #4b5263; -fx-padding: 5;"
            prefHeight = 30.0
            minHeight = 30.0
            alignment = Pos.CENTER_LEFT
            children.add(Label("Drag Header").apply {
                style = "-fx-text-fill: white; -fx-font-weight: bold;"
            })
        }

        // Content (Not draggable)
        val content = VBox().apply {
            style = "-fx-background-color: #3e4451; -fx-alignment: center;"
            minWidth = 15.0
            VBox.setVgrow(this, Priority.ALWAYS)
            children.add(Label("Content Area").apply {
                style = "-fx-text-fill: #abb2bf;"
            })
        }

        header.setOnDragDetected { event ->
            val db = header.startDragAndDrop(TransferMode.MOVE)
            val contentClip = ClipboardContent()
            contentClip.putString("FILE_TREE_PANEL")
            db.setContent(contentClip)
            event.consume()
        }

        root.children.addAll(header, content)
        return root
    }

    private fun setupWorkbench() {
        viewModel.sidebarPosition.addListener { _, _, _ -> rebuildWorkbench() }
        viewModel.sidebarVisible.addListener { _, _, _ -> rebuildWorkbench() }

        rebuildWorkbench()
    }

    private fun rebuildWorkbench() {
        workbenchPane.items.clear()

        val isSidebarVisible = viewModel.sidebarVisible.get()
        val position = viewModel.sidebarPosition.get()

        if (!isSidebarVisible) {
            workbenchPane.items.add(editorPlaceholder)
            return
        }

        val size = viewModel.sidebarSize.get()

        // Logic to position File Tree vs Editor
        when (position) {
            PanelPosition.LEFT -> {
                workbenchPane.orientation = Orientation.HORIZONTAL
                workbenchPane.items.addAll(fileTreePlaceholder, editorPlaceholder)
            }
            PanelPosition.RIGHT -> {
                workbenchPane.orientation = Orientation.HORIZONTAL
                workbenchPane.items.addAll(editorPlaceholder, fileTreePlaceholder)
            }
            PanelPosition.TOP -> {
                workbenchPane.orientation = Orientation.VERTICAL
                workbenchPane.items.addAll(fileTreePlaceholder, editorPlaceholder)
            }
            PanelPosition.BOTTOM -> {
                workbenchPane.orientation = Orientation.VERTICAL
                workbenchPane.items.addAll(editorPlaceholder, fileTreePlaceholder)
            }
        }
        
        // Prevents splitpane moving when window changes size as well as resetting size on reopen with too small areas
        // Thx StackOverflow
        SplitPane.setResizableWithParent(fileTreePlaceholder, false)
        SplitPane.setResizableWithParent(editorPlaceholder, false)

        // Helper to apply position and THEN attach listener to avoid initial drift
        val attachListener = {
            if (workbenchPane.dividers.isNotEmpty()) {
                workbenchPane.dividers[0].positionProperty().addListener { _, _, newPos ->
                    val newSize = newPos.toDouble()
                    val normalizedSize = if (position == PanelPosition.RIGHT || position == PanelPosition.BOTTOM) {
                        1.0 - newSize
                    } else {
                        newSize
                    }
                    // Only update if significantly different to avoid feedback loops
                    if (abs(viewModel.sidebarSize.get() - normalizedSize) > 0.001) {
                        viewModel.sidebarSize.set(normalizedSize)
                    }
                }
            }
        }

        if (workbenchPane.width > 0 && workbenchPane.height > 0) {
            applyDividerPosition(position, size)
            attachListener()
        } else {
             val layoutListener = object : ChangeListener<Number> {
                 override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
                     if (newValue!!.toDouble() > 0) {
                         workbenchPane.widthProperty().removeListener(this)
                         workbenchPane.heightProperty().removeListener(this)

                         applyDividerPosition(position, size)
                         attachListener()
                     }
                 }
             }
             workbenchPane.widthProperty().addListener(layoutListener)
             workbenchPane.heightProperty().addListener(layoutListener)
        }
    }

    private fun applyDividerPosition(position: PanelPosition, size: Double) {
        val divPos = if (position == PanelPosition.RIGHT || position == PanelPosition.BOTTOM) {
            1.0 - size
        } else {
            size
        }
        workbenchPane.setDividerPositions(divPos)
    }

    private fun setupDragAndDrop() {
        workbenchPane.setOnDragOver { event ->
            if (event.dragboard.hasString() && event.dragboard.string == "FILE_TREE_PANEL") {
                val pos = viewModel.calculateDropPosition(event.x, event.y, workbenchPane.width, workbenchPane.height)
                if (pos != null) {
                    event.acceptTransferModes(TransferMode.MOVE)
                }

                // Highlight active zone
                zoneHighlighter?.invoke(pos)
            }
            event.consume()
        }

        workbenchPane.setOnDragExited {
            // Clear highlight
            zoneHighlighter?.invoke(null)
        }

        workbenchPane.setOnDragDropped { event ->
            val db = event.dragboard
            var success = false
            if (db.hasString() && db.string == "FILE_TREE_PANEL") {
                success = viewModel.onPanelDropped(event.x, event.y, workbenchPane.width, workbenchPane.height)
            }
            // Clear highlight
            zoneHighlighter?.invoke(null)

            event.isDropCompleted = success
            event.consume()
        }
    }

    private fun createDebugRegion(name: String, colorHex: String, width: Double? = null, height: Double? = null): Pane {
        return VBox().apply {
            style = "-fx-background-color: $colorHex; -fx-alignment: center;"
            minWidth = 15.0
            children.add(Label(name).apply { style = "-fx-font-weight: bold; -fx-text-fill: black;" })

            if (width != null) {
                prefWidth = width
                minWidth = width
                maxWidth = width
            }
            if (height != null) {
                prefHeight = height
                minHeight = height
                maxHeight = height
            }
        }
    }
}
