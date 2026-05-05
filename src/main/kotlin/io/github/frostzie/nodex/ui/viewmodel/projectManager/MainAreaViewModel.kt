package io.github.frostzie.nodex.ui.viewmodel.projectManager

import javafx.stage.DirectoryChooser
import javafx.stage.Window
import java.nio.file.Path

class MainAreaViewModel(
    private val parent: ProjectManagerViewModel
) {
    fun onImportClick(ownerWindow: Window) {
        val chooser = DirectoryChooser().apply {
            title = "Select Project Directory"
        }

        val selectedDirectory = chooser.showDialog(ownerWindow)
        if (selectedDirectory != null) {
            val path: Path = selectedDirectory.toPath()
            parent.onImportProject(path)
        }
    }
}
