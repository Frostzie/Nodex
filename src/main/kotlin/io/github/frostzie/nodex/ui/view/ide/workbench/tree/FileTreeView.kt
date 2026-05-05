package io.github.frostzie.nodex.ui.view.ide.workbench.tree

import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.tree.FileTreeItem
import io.github.frostzie.nodex.ui.viewmodel.ide.workbench.tree.FileTreeViewModel
import atlantafx.base.theme.Styles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.input.MouseEvent
import java.nio.file.Path

class FileTreeView(
    private val viewModel: FileTreeViewModel,
    private val onFileOpen: (Path) -> Unit
) : VBox() {
    private val header = HBox()
    private val treeView = TreeView<FileTreeItem>()

    init {
        setupHeader()
        setupTreeView()

        children.addAll(header, treeView)
    }

    private fun setupHeader() {
        header.minHeight = 30.0
        header.prefHeight = 30.0
        header.maxHeight = 30.0

        val label = Label("Project")
        label.styleClass.addAll(Styles.TEXT_CAPTION)
        header.padding = Insets(0.0, 0.0, 0.0, 4.0)
        header.alignment = Pos.CENTER_LEFT
        header.children.add(label)

        // Reimplementing the drag logic for the Workbench
        header.setOnDragDetected { event ->
            val db = header.startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()
            content.putString(ToolWindow.FILES.name)
            db.setContent(content)
            event.consume()
        }
    }

    private fun setupTreeView() {
        treeView.rootProperty().bind(viewModel.root)
        treeView.isShowRoot = true //TODO: Need to add Project name next to root similar to IntelliJ

        treeView.selectionModel.selectedItemProperty().addListener { _, _, newItem ->
            viewModel.selectedPath.set(newItem?.value?.path)
        }

        viewModel.selectedPath.addListener { _, _, newPath ->
            if (newPath == null) {
                treeView.selectionModel.clearSelection()
            } else {
                val item = viewModel.nodeCache[newPath.toAbsolutePath().toString()]
                if (item != null) {
                    treeView.selectionModel.select(item)
                } else {
                    treeView.selectionModel.clearSelection()
                }
            }
        }

        treeView.setCellFactory {
            object : TreeCell<FileTreeItem>() {
                override fun updateItem(item: FileTreeItem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {
                        text = item.displayName
                    }
                }
            }
        }

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED) { event ->
            if (event.clickCount == 2) {
                val selectedItem = treeView.selectionModel.selectedItem
                if (selectedItem != null && !selectedItem.value.isDirectory) {
                    onFileOpen(selectedItem.value.path)
                }
            }
        }

        setVgrow(treeView, Priority.ALWAYS)
    }
}
