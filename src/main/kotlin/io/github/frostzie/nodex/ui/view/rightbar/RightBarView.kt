package io.github.frostzie.nodex.ui.view.rightbar

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedMZ

class RightBarView : VBox() {

    init {
        prefWidth = 40.0
        minWidth = 40.0
        maxWidth = 40.0
        styleClass.add("right-bar")

        val bottomBtnArea = VBox().apply {
            alignment = Pos.TOP_CENTER


            val notifBtn = Button(null, FontIcon(Material2OutlinedMZ.NOTIFICATIONS_NONE))
            notifBtn.styleClass.addAll(
                Styles.FLAT,
                Styles.BUTTON_ICON
            )

            notifBtn.onAction


            children.add(notifBtn)
            padding = Insets(2.0)
        }

        val spacer = Spacer()
        setVgrow(spacer, Priority.ALWAYS)

        children.addAll(spacer, bottomBtnArea)
    }
}
