package io.github.frostzie.nodex.ui.view.projectManager

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HeaderBar
import javafx.scene.layout.HBox

/**
 * The top bar for the Project Manager screen.
 */
class ProjectManagerTopBarView : HeaderBar() {

    init {
        styleClass.add("project-manager-top-bar")
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        prefHeight = 40.0

        val icon = ImageView().apply {
            image = Image("assets/nodex/icon.png")
            fitWidth = 20.0
            fitHeight = 20.0
            isPreserveRatio = true
        }

        val title = Label("Project Manager")

        val leftBox = HBox(10.0, icon, title).apply {
            alignment = Pos.CENTER_LEFT
        }
        left = leftBox
    }
}
