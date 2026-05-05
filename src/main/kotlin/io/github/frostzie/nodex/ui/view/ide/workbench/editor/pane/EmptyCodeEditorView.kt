package io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

//TODO: Add more desc + options similar to IntelliJ or VsCode ig
class EmptyCodeEditorView : StackPane() {

    init {
        val label = Label("Open a file to continue.")

        this.children.add(label)
        setAlignment(label, Pos.CENTER)
    }
}