package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import io.github.frostzie.nodex.api.misc.Styling
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.navigation.OverlayStage
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle

import io.github.frostzie.nodex.ui.ViewFactory
import io.github.frostzie.nodex.utils.LoggerProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

/**
 * Services are resolved from Koin.
 * Exposes typed getters so nobody needs to import Koin directly.
 */
object ServiceBootstrap : KoinComponent {
    private val logger = LoggerProvider.getLogger("ServiceBootstrap")

    val modVersionService: ModVersion by inject()
    val concurrencyService: Concurrency by inject()
    val fileWatcherService: FileWatcher by inject()
    val performanceService: PerformanceMonitor by inject()
    val configService: Config by inject()
    val settingsService: Settings by inject()
    val navigationService: Navigation by inject()
    val recentProjectsService: RecentProjects by inject()
    val workspaceLifecycle: WorkspaceLifecycle by inject()
    val stylingService: Styling by inject()
    val mainStage: MainStage by inject()
    val overlayStage: OverlayStage by inject()
    val viewFactory: ViewFactory by inject()

    fun start() {
        logger.info("Starting services...")
        startKoin { modules(appModule) }

        modVersionService.initialize()
        performanceService.initialize()
        fileWatcherService.initialize()
        configService.initialize()
        settingsService.initialize()
        recentProjectsService.initialize()
    }

    fun stop() {
        logger.info("Shutting down services...")
        workspaceLifecycle.shutdown()
        fileWatcherService.shutdown()
        concurrencyService.shutdown()
    }
}
