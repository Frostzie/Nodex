package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.services.core.ConfigService
import io.github.frostzie.nodex.services.core.ConcurrencyService
import io.github.frostzie.nodex.services.core.FileService
import io.github.frostzie.nodex.services.core.PerformanceService
import io.github.frostzie.nodex.services.workspace.EditorService
import io.github.frostzie.nodex.services.core.LayoutService
import io.github.frostzie.nodex.services.core.ModInfoService
import io.github.frostzie.nodex.services.ui.NavigationService
import io.github.frostzie.nodex.services.ui.StylingService
import io.github.frostzie.nodex.services.files.FileWatcherService
import io.github.frostzie.nodex.services.core.SessionService
import io.github.frostzie.nodex.services.workspace.WorkspaceService

/**
 * Responsible for initializing core logic services.
 */
object ServiceBootstrap {
    lateinit var configService: ConfigService
    lateinit var concurrencyService: ConcurrencyService
    lateinit var modInfoService: ModInfoService
    lateinit var fileWatcherService: FileWatcherService
    lateinit var fileService: FileService
    lateinit var sessionService: SessionService
    lateinit var layoutService: LayoutService
    lateinit var navigationService: NavigationService
    lateinit var stylingService: StylingService
    lateinit var editorService: EditorService
    lateinit var workspaceService: WorkspaceService
    lateinit var performanceService: PerformanceService

    fun start() {
        // Instantiate services in dependency order
        concurrencyService = ConcurrencyService()
        modInfoService = ModInfoService()
        performanceService = PerformanceService()
        configService = ConfigService()
        fileWatcherService = FileWatcherService()
        fileService = FileService(fileWatcherService)
        sessionService = SessionService(configService)
        layoutService = LayoutService(configService, fileService)
        navigationService = NavigationService(concurrencyService)
        stylingService = StylingService()
        editorService = EditorService(concurrencyService, fileService)
        
        workspaceService = WorkspaceService(
            configService,
            sessionService,
            editorService,
            fileWatcherService,
            concurrencyService
        )

        // Initialize them
        modInfoService.initialize()
        configService.initialize()
        sessionService.initialize()
        layoutService.initialize()
        navigationService.initialize()
        workspaceService.initialize()
        editorService.initialize()
        performanceService.initialize()
    }
}
