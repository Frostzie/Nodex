package io.github.frostzie.nodex.ui.view.settings

import io.github.frostzie.nodex.ui.utils.extensions.withMaxLength
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.HeaderBar

class SettingsTopBarView(
    vm: SettingsCategoryViewModel
) : HeaderBar() {
    private val scopeDropdown = ComboBox<String>()
    private val searchField = TextField()

    init {
        styleClass.add("settings-top-bar")
        padding = Insets(4.0, 2.0, 4.0, 2.0)

        scopeDropdown.items.setAll("Global Settings")
        scopeDropdown.value = "Global Settings"
        scopeDropdown.isDisable = true // For now since project specific configs aren't a thing yet

        searchField.promptText = "Search settings"
        searchField.textProperty().bindBidirectional(vm.searchQuery)
        searchField.maxWidth = 240.0
        searchField.isFocusTraversable = false
        searchField.withMaxLength(100)

        val leftBox = HBox(scopeDropdown).apply {
            alignment = Pos.CENTER_LEFT
            isPickOnBounds = false
        }

        left = leftBox
        center = searchField
    }
}
