package io.github.frostzie.nodex.ui.view.topbar

import javafx.geometry.Pos
import javafx.scene.layout.HBox

class TopBarView : HBox() {

    init {
        prefHeight = 35.0
        minHeight = 35.0
        maxHeight = 35.0
        alignment = Pos.CENTER_LEFT
        styleClass.add("top-bar")
    }
}