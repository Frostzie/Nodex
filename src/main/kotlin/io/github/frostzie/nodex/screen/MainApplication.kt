package io.github.frostzie.nodex.screen

import io.github.frostzie.nodex.events.EventBus
import io.github.frostzie.nodex.events.WorkspaceUpdated
import io.github.frostzie.nodex.handlers.bars.BottomBarHandler
import io.github.frostzie.nodex.handlers.bars.LeftBarHandler
import io.github.frostzie.nodex.handlers.bars.top.TopBarHandler
import io.github.frostzie.nodex.handlers.popup.file.FilePopupHandler
import io.github.frostzie.nodex.handlers.popup.settings.SettingsHandler
import io.github.frostzie.nodex.modules.bars.BottomBarModule
import io.github.frostzie.nodex.modules.bars.LeftBarModule
import io.github.frostzie.nodex.modules.bars.top.TopBarViewModel
import io.github.frostzie.nodex.modules.popup.file.FilePopupModule
import io.github.frostzie.nodex.modules.popup.settings.SettingsModule
import io.github.frostzie.nodex.handlers.popup.settings.ThemeHandler
import io.github.frostzie.nodex.modules.popup.settings.ThemeModule
import io.github.frostzie.nodex.styling.common.NotificationMessageArea
import io.github.frostzie.nodex.screen.elements.bars.BottomBarView
import io.github.frostzie.nodex.screen.elements.bars.LeftBarView
import io.github.frostzie.nodex.screen.elements.bars.top.TopBarView
import io.github.frostzie.nodex.screen.elements.main.FileTreeView
import io.github.frostzie.nodex.screen.elements.main.TextEditorView
import io.github.frostzie.nodex.screen.elements.popup.settings.SettingsView
import io.github.frostzie.nodex.screen.elements.project.ProjectManagerView
import io.github.frostzie.nodex.project.WorkspaceManager
import io.github.frostzie.nodex.settings.annotations.SubscribeEvent
import io.github.frostzie.nodex.utils.JavaFXInitializer
import io.github.frostzie.nodex.utils.LoggerProvider
import io.github.frostzie.nodex.utils.WindowResizer
import io.github.frostzie.nodex.utils.dev.DebugManager
import javafx.scene.layout.Pane
import io.github.frostzie.nodex.utils.CSSManager
import io.github.frostzie.nodex.utils.WindowDrag
import io.github.frostzie.nodex.utils.UIConstants
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.control.SplitPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import io.github.frostzie.nodex.settings.categories.ThemeConfig
import io.github.frostzie.nodex.utils.ThemeUtils
import io.github.frostzie.nodex.config.LayoutManager

class MainApplication {

    companion object {
        private val logger = LoggerProvider.getLogger("MainApplication")
        private var primaryStage: Stage? = null
        private var fxInitialized = false

        // UI Components
        private var topBarView: TopBarView? = null
        private var leftBarView: LeftBarView? = null
        private var fileTreeView: FileTreeView? = null
        private var bottomBarView: BottomBarView? = null
        private var settingsView: SettingsView? = null
        private var textEditorView: TextEditorView? = null
        private var contentArea: SplitPane? = null
        
        // View Containers
        private var projectManagerView: ProjectManagerView? = null
        private var ideLayout: BorderPane? = null
        private var rootContainer: StackPane? = null

        // Modules and Handlers
        private var topBarViewModel: TopBarViewModel? = null
        private var topBarHandler: TopBarHandler? = null
