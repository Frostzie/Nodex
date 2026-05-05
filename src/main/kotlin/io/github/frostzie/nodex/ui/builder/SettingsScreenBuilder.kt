package io.github.frostzie.nodex.ui.builder

import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.settings.registry.CategorySpec
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.ui.builder.settings.CoreSettingsPanels
import io.github.frostzie.nodex.ui.utils.settings.SettingsSearchEntry
import io.github.frostzie.nodex.ui.view.layout.SettingsLayoutView
import io.github.frostzie.nodex.ui.view.settings.SettingsActionsBarView
import io.github.frostzie.nodex.ui.view.settings.SettingsCategoryView
import io.github.frostzie.nodex.ui.view.settings.SettingsContentHostView
import io.github.frostzie.nodex.ui.view.settings.SettingsTopBarView
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsActionsBarViewModel
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryNode
import io.github.frostzie.nodex.ui.viewmodel.settings.SettingsCategoryViewModel
import javafx.scene.layout.Region

class SettingsScreenBuilder(
    private val settingsService: Settings,
    private val navigationService: Navigation,
    private val settingsRegistry: SettingsRegistry
) : OverlayBuilder {
    override val screen: OverlayScreen = OverlayScreen.SETTINGS

    override fun build(): Region {
        settingsService.discard()

        val bindings = CoreSettingsPanels.bindings
        val viewModels = bindings.associate { binding ->
            binding.categoryId to binding.viewModelFactory(settingsService, settingsRegistry)
        }
        viewModels.values.forEach { it.initializeFromSettings(settingsService.committed) }

        val actionsViewModel = SettingsActionsBarViewModel(
            settingsService,
            viewModels.values.toList(),
            navigationService
        )

        val panelIds = viewModels.keys
        val categoryRoot = buildCategoryTree(settingsRegistry, panelIds = panelIds)
        val searchIndex = settingsRegistry.allSpecs()
            .groupBy { it.categoryId }
            .mapValues { (_, specs) ->
                specs.map { spec ->
                    SettingsSearchEntry(spec.id, spec.title, spec.description)
                }
            }
        val categoryViewModel = SettingsCategoryViewModel(categoryRoot, searchIndex)

        val realPanels = bindings.associate { binding ->
            val viewModel = viewModels[binding.categoryId]
                ?: error("Missing ViewModel for category: ${binding.categoryId}")
            binding.categoryId to binding.panelFactory(viewModel, categoryViewModel.searchQuery, settingsRegistry)
        }

        val categoryView = SettingsCategoryView(categoryViewModel)
        val topBarView = SettingsTopBarView(categoryViewModel)
        val contentHost = SettingsContentHostView(
            categoryViewModel = categoryViewModel,
            panels = realPanels
        )
        val actionsBarView = SettingsActionsBarView(actionsViewModel)
        return SettingsLayoutView(categoryView, topBarView, contentHost, actionsBarView)
    }

    private fun buildCategoryTree(registry: SettingsRegistry, panelIds: Set<String>): SettingsCategoryNode {
        fun buildNode(categorySpec: CategorySpec): SettingsCategoryNode? {
            // Leaf categories are only included if they have a panel
            if (categorySpec.isLeaf) {
                if (categorySpec.id !in panelIds) return null
                return SettingsCategoryNode(
                    id = categorySpec.id,
                    label = categorySpec.label,
                    panelId = categorySpec.id,
                    children = emptyList(),
                    searchable = true
                )
            }

            // Branch categories are only included if they have includable children
            val children = registry.childrenOf(categorySpec.id).mapNotNull { buildNode(it) }
            if (children.isEmpty()) return null
            return SettingsCategoryNode(
                id = categorySpec.id,
                label = categorySpec.label,
                panelId = null,
                children = children,
                searchable = true
            )
        }

        val rootChildren = registry.childrenOf(null).mapNotNull { buildNode(it) }
        return SettingsCategoryNode(
            id = "root",
            label = "Settings",
            searchable = false,
            children = rootChildren
        )
    }
}