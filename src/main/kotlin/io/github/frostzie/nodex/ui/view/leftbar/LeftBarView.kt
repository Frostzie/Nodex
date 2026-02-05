package io.github.frostzie.nodex.ui.view.leftbar

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import io.github.frostzie.nodex.ui.viewmodel.leftbar.LeftBarViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL

class LeftBarView : VBox() {
    private val viewModel = LeftBarViewModel()

    init {
        prefWidth = 40.0
        minWidth = 40.0
        maxWidth = 40.0


        val topBtnArea = VBox().apply {
            alignment = Pos.TOP_CENTER

            val toggleFileTreeBtn = Button(null, FontIcon(Material2OutlinedAL.FOLDER)).apply {
                setOnAction { viewModel.toggleSidebar() }
            }

            toggleFileTreeBtn.styleClass.addAll(
                Styles.FLAT,
                Styles.BUTTON_ICON
            )

            children.add(toggleFileTreeBtn)
            padding = Insets(2.0)
        }

        val bottomBtnArea = VBox().apply {
            alignment = Pos.TOP_CENTER

            //TODO: Imp open log/debug screen
            val consoleBtn = Button(null, FontIcon(Material2OutlinedAL.ARTICLE)).apply {
                isDisable = false
            }

            consoleBtn.styleClass.addAll(
                Styles.FLAT,
                Styles.BUTTON_ICON
            )

            consoleBtn.onAction

            children.add(consoleBtn)
            padding = Insets(2.0)
        }

        val spacer = Spacer()
        setVgrow(spacer, Priority.ALWAYS)

        children.addAll(topBtnArea, spacer, bottomBtnArea)
    }
}