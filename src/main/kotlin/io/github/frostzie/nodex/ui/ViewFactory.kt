package io.github.frostzie.nodex.ui

import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.ui.builder.IdeScreenBuilder
import io.github.frostzie.nodex.ui.builder.IntroScreenBuilder
import io.github.frostzie.nodex.ui.builder.OverlayBuilder
import io.github.frostzie.nodex.ui.builder.ProjectManagerScreenBuilder
import io.github.frostzie.nodex.ui.builder.SettingsScreenBuilder
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

/**
 * Factory for creating and assembling UI components.
 */
class ViewFactory(
    private val navigationService: Navigation,
    configService: Config,
    layoutService: Layout,
    performanceService: PerformanceMonitor,
    settingsService: Settings,
    fileTreeService: FileTree,
    projectRuntimeService: ProjectRuntime,
    fileTreePersistenceService: FileTreePersistence,
    recentProjects: RecentProjects,
    workspaceLifecycle: WorkspaceLifecycle,
    settingsRegistry: SettingsRegistry,
    editorSession: EditorSession,
    mainStage: MainStage,
) {
    private val ideBuilder = IdeScreenBuilder(
        layoutService,
        navigationService,
        performanceService,
        fileTreeService,
        projectRuntimeService,
        fileTreePersistenceService,
        editorSession,
        workspaceLifecycle,
        mainStage
    )
    private val projectManagerBuilder = ProjectManagerScreenBuilder(
        navigationService,
        workspaceLifecycle,
        recentProjects
    )
    private val introBuilder = IntroScreenBuilder(navigationService, configService)
    private val settingsBuilder = SettingsScreenBuilder(
        settingsService,
        navigationService,
        settingsRegistry
    )
    private val overlayBuilders: Map<OverlayScreen, OverlayBuilder> =
        listOf(settingsBuilder).associateBy { it.screen }

    fun createOverlayContent(screen: OverlayScreen): Region {
        return requireNotNull(overlayBuilders[screen]) {
            "No overlay builder registered for screen: $screen"
        }.build()
    }

    fun createScreenHost(): ScreenHost {
        val ideLayout = ideBuilder.build()
        val projectManagerLayout = projectManagerBuilder.build()
        val introLayoutView = introBuilder.build()
        return ScreenHost(ideLayout, introLayoutView, projectManagerLayout, navigationService)
    }

    fun createRootView(screenHost: ScreenHost): Region {
        return StackPane(screenHost)
    }
}
