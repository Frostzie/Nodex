package io.github.frostzie.nodex.ui.view.bottombar

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import atlantafx.base.theme.Tweaks
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.kordamp.ikonli.javafx.FontIcon
import javafx.beans.binding.Bindings
import javafx.geometry.Side
import org.kordamp.ikonli.material2.Material2OutlinedAL
import io.github.frostzie.nodex.ui.viewmodel.bottombar.BottomBarViewModel

class BottomBarView : HBox() {
    val viewModel = BottomBarViewModel()
    private val ideVersionLabel = Label().apply { padding = Insets(0.0, 0.0, 0.0, 5.0) }
    private val readOnlyToggle = Button()
    private val performanceMenu = MenuButton()

    init {
        prefHeight = 25.0
        minHeight = 25.0
        maxHeight = 25.0
        alignment = Pos.CENTER_LEFT

        bind()

        val editorInfoArea = HBox().apply {
            //TODO: Change icon to be easier to notice the difference
            val lockedIcon = FontIcon(Material2OutlinedAL.LOCK)
            val unlockedIcon = FontIcon(Material2OutlinedAL.LOCK_OPEN)

            readOnlyToggle.styleClass.addAll(
                Styles.FLAT,
                Styles.BUTTON_ICON
            )
            readOnlyToggle.setMaxSize(24.0, 24.0)
            readOnlyToggle.setMinSize(24.0, 24.0)
            readOnlyToggle.setPrefSize(24.0, 24.0)

            // Bind for changing icon
            readOnlyToggle.graphicProperty().bind(Bindings.createObjectBinding({
                if (viewModel.isDocumentLocked.get()) lockedIcon else unlockedIcon
            }, viewModel.isDocumentLocked))

            readOnlyToggle.setOnAction { viewModel.toggleFileLock() }

            children.add(readOnlyToggle)
        }

        val spacer = Spacer()
        setHgrow(spacer, Priority.ALWAYS)
        val separator = Separator(Orientation.VERTICAL)

        children.addAll(ideVersionLabel, spacer, editorInfoArea, separator, performanceMenu)
    }

    private fun bind() {
        // Mod Info Area
        ideVersionLabel.textProperty().bind(viewModel.ideVersionProperty)

        // Editor Info Area
        readOnlyToggle.visibleProperty().bind(viewModel.isDocumentPresent)
        readOnlyToggle.managedProperty().bind(viewModel.isDocumentPresent)

        // Performance Area
        //TODO: Fix button size being too big
        //TODO: Add CPU usage
        //TODO: Save to config to save chosen option
        performanceMenu.styleClass.addAll(Styles.FLAT, Tweaks.NO_ARROW)
        performanceMenu.popupSide = Side.TOP

        val fpsItem = MenuItem()
        fpsItem.textProperty().bind(viewModel.fpsProperty)
        fpsItem.setOnAction { performanceMenu.textProperty().bind(viewModel.fpsProperty) }

        val memItem = MenuItem()
        memItem.textProperty().bind(viewModel.memoryProperty)
        memItem.setOnAction { performanceMenu.textProperty().bind(viewModel.memoryProperty) }

        performanceMenu.items.addAll(fpsItem, memItem)

        // sets fps as default
        performanceMenu.textProperty().bind(viewModel.fpsProperty)
    }
}