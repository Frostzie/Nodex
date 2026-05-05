package io.github.frostzie.nodex.ui.view.settings

import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsActionsBarViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

/**
 * View for the settings actions bar containing Apply, Save, and Discard buttons (BottomBar).
 */
class SettingsActionsBarView(
    private val vm: SettingsActionsBarViewModel
) : BorderPane() {

    init {
        styleClass.add("settings-actions-bar")
        padding = Insets(8.0, 2.0, 8.0, 2.0)

        val discardBtn = Button("Discard").apply {
            disableProperty().bind(vm.isDirty.not())
            setOnAction { vm.discard() }
        }

        val applyBtn = Button("Apply").apply {
            disableProperty().bind(vm.isDirty.not().or(vm.isValid.not()))
            setOnAction { vm.apply() }
        }

        val saveBtn = Button("Save").apply {
            setOnAction { vm.save() }
        }

        left = HBox(discardBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        right = HBox(8.0, applyBtn, saveBtn).apply {
            alignment = Pos.CENTER_RIGHT
        }
    }
}
