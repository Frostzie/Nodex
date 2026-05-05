package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.ui.view.settings.SettingsActionsBarView
import io.github.frostzie.nodex.ui.view.settings.SettingsCategoryView
import io.github.frostzie.nodex.ui.view.settings.SettingsContentHostView
import io.github.frostzie.nodex.ui.view.settings.SettingsTopBarView
import javafx.scene.layout.BorderPane

/**
 * The Settings Layout View.
 */
class SettingsLayoutView(
    categoryView: SettingsCategoryView,
    topBarView: SettingsTopBarView,
    contentView: SettingsContentHostView,
    actionsBarView: SettingsActionsBarView
) : BorderPane() {

    init {
        top = topBarView
        left = categoryView
        center = contentView
        bottom = actionsBarView
    }
}
