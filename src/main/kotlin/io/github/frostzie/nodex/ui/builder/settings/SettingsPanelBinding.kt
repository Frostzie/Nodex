package io.github.frostzie.nodex.ui.builder.settings

import io.github.frostzie.nodex.api.settings.SettingsAccess
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.ui.view.settings.GenericCategoryView
import io.github.frostzie.nodex.ui.viewmodel.settings.GenericSettingsPanelViewModel
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsPanelViewModel
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.ScrollPane

/**
 * Binds a settings category to its ViewModel and panel.
 *
 * Core categories register bindings here, custom categories can add bindings too.
 */
data class SettingsPanelBinding(
    val categoryId: String,
    val viewModelFactory: (SettingsAccess, SettingsRegistry) -> SettingsPanelViewModel,
    val panelFactory: (SettingsPanelViewModel, StringProperty, SettingsRegistry) -> Node
) {
    companion object {

        /**
         * @param categoryId The settings category this panel manages.
         * @param panelFactory Optional custom panel factory. Defaults to [GenericCategoryView].
         */
        fun generic(
            categoryId: String,
            panelFactory: (GenericSettingsPanelViewModel, StringProperty, SettingsRegistry) -> Node =
                { vm, searchQuery, registry ->
                    ScrollPane(GenericCategoryView(vm, searchQuery, registry)).apply {
                        isFitToWidth = true
                    }
                }
        ): SettingsPanelBinding {
            return SettingsPanelBinding(
                categoryId = categoryId,
                viewModelFactory = { settingsService, registry ->
                    GenericSettingsPanelViewModel(
                        categoryId = categoryId,
                        settingsService = settingsService,
                        registry = registry,
                    )
                },
                panelFactory = { viewModel, searchQuery, registry ->
                    panelFactory(viewModel as GenericSettingsPanelViewModel, searchQuery, registry)
                }
            )
        }
    }
}
