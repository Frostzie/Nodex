package io.github.frostzie.nodex.ui.view.projectManager

import atlantafx.base.theme.Styles
import io.github.frostzie.nodex.ui.viewmodel.projectManager.MainAreaViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox

class MainAreaView(
    private val viewModel: MainAreaViewModel
) : VBox(25.0) {
    init {
        alignment = Pos.CENTER
        padding = Insets(40.0)

        val icon = ImageView().apply {
            image = Image("assets/nodex/icon.png")
            fitHeight = 160.0
            fitWidth = 160.0
            isPreserveRatio = true
        }

        val createBtn = Button("Create New").apply {
            prefWidth = 250.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            isDisable = true
        }

        val importBtn = Button("Open").apply {
            prefWidth = 250.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            setOnAction {
                viewModel.onImportClick(scene.window)
            }
        }

        val cloneBtn = Button("Clone Repository").apply {
            prefWidth = 250.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            isDisable = true
        }

        val syncInstance = Button("Sync Instance").apply {
            prefWidth = 250.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            tooltip = Tooltip("Sync with universal folder.")
            isDisable = true
        }

        children.addAll(icon, createBtn, importBtn, cloneBtn, syncInstance)
    }
}
