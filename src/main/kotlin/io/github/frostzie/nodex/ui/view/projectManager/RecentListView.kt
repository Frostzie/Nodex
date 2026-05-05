package io.github.frostzie.nodex.ui.view.projectManager

import io.github.frostzie.nodex.domain.entity.RecentProject
import io.github.frostzie.nodex.ui.viewmodel.projectManager.ProjectManagerViewModel
import atlantafx.base.theme.Styles
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.input.MouseButton
import javafx.scene.control.ListCell
import kotlin.io.path.name

/**
 * The sidebar containing the list of recent projects.
 */
class RecentListView(
    private val viewModel: ProjectManagerViewModel
) : VBox() {
    init {
        prefWidth = 300.0
        maxWidth = 300.0
        minWidth = 200.0

        val titleLabel = Label("Recent Projects").apply {
            styleClass.addAll(Styles.TITLE_4, Styles.TEXT_MUTED)
            padding = Insets(10.0)
            maxWidth = Double.MAX_VALUE
        }

        //TODO: Remake to look like IntelliJ
        val listView = ListView<RecentProject>().apply {
            setVgrow(this, Priority.ALWAYS)
            items = viewModel.recentProjects
            setCellFactory {
                object : ListCell<RecentProject>() {
                    override fun updateItem(item: RecentProject?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty || item == null) null else item.path.name
                    }
                }
            }

            setOnMouseClicked { event ->
                if (event.button == MouseButton.PRIMARY && event.clickCount == 2) {
                    selectionModel.selectedItem?.let { selected ->
                        viewModel.onOpenRecentProject(selected.path)
                    }
                }
            }
        }

        children.addAll(titleLabel, listView)
    }
}
