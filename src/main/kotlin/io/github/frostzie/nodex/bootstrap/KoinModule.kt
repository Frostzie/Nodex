package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.config.Migration
import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.config.LayoutPersistence
import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.navigation.ToolWindowProvider
import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.OverlayStage
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.api.misc.Styling
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle

import io.github.frostzie.nodex.services.config.BackupService
import io.github.frostzie.nodex.services.config.ConfigLocationService
import io.github.frostzie.nodex.services.config.ConfigMoveService
import io.github.frostzie.nodex.services.config.MigrationService
import io.github.frostzie.nodex.services.config.global.ProjectsConfigService
import io.github.frostzie.nodex.services.config.global.RecentProjectsService
import io.github.frostzie.nodex.services.config.global.SettingsConfigService
import io.github.frostzie.nodex.services.config.project.LayoutConfigService
import io.github.frostzie.nodex.services.config.project.ProjectConfigService
import io.github.frostzie.nodex.services.config.project.TreeConfigService
import io.github.frostzie.nodex.services.config.stationary.ConfigService
import io.github.frostzie.nodex.services.core.ConcurrencyService
import io.github.frostzie.nodex.services.core.FileService
import io.github.frostzie.nodex.services.core.LayoutService
import io.github.frostzie.nodex.services.core.ModVersionService
import io.github.frostzie.nodex.services.core.PerformanceService
import io.github.frostzie.nodex.services.files.FileTreePersistenceService
import io.github.frostzie.nodex.services.files.FileTreeService
import io.github.frostzie.nodex.services.files.FileWatcherService
import io.github.frostzie.nodex.services.settings.SettingsService
import io.github.frostzie.nodex.services.settings.SettingsValidationService
import io.github.frostzie.nodex.services.ui.FocusService
import io.github.frostzie.nodex.services.ui.NavigationService
import io.github.frostzie.nodex.services.ui.StylingService
import io.github.frostzie.nodex.services.ui.ToolWindowService
import io.github.frostzie.nodex.services.ui.WindowProfileService
import io.github.frostzie.nodex.services.ui.MainStageService
import io.github.frostzie.nodex.services.ui.OverlayStageService
import io.github.frostzie.nodex.services.workspace.ProjectRuntimeService
import io.github.frostzie.nodex.services.workspace.EditorSessionService
import io.github.frostzie.nodex.services.workspace.WorkspaceLifecycleService

import io.github.frostzie.nodex.domain.config.ConfigPaths
import io.github.frostzie.nodex.loader.fabric.Folders
import io.github.frostzie.nodex.ui.ViewFactory
import org.koin.dsl.module

/**
 * Koin module for all services.
 */
val appModule = module {
    single<ModVersion> { ModVersionService() }
    single<Migration> { MigrationService() }
    single<Concurrency> { ConcurrencyService() }
    single<FocusTracker> { FocusService() }
    single<PerformanceMonitor> { PerformanceService() }
    single<Navigation> { NavigationService(get()) }
    single<FileWatcher> { FileWatcherService(get(), get()) }
    single<FileOperations> { FileService(get()) }
    single<WindowProfile> { WindowProfileService() }
    single<ToolWindowProvider> { ToolWindowService(get()) }
    single<Layout> { LayoutService(get(), get(), get()) }
    single<Config> { ConfigService(Folders.configDir, get(), get()) }
    single { ConfigMoveService(get()) }
    single { BackupService(get()) }
    single { SettingsValidationService(SettingsBootstrap.settingsRegistry) }
    single { ConfigLocationService(get(), get(), get()) }

    single<ConfigPaths> {
        val configRoot = Folders.configDir
        val configLocationService: ConfigLocationService = get()
        val effectiveNodexDir = configLocationService.resolveEffectiveNodexDir(configRoot.resolve("nodex"))
        ConfigPaths(effectiveNodexDir)
    }

    single<SettingsConfigService> {
        val paths: ConfigPaths = get()
        SettingsConfigService(
            settingsPath = paths.root.resolve("settings.json"),
            backupDir = paths.root.resolve("backups"),
            fileOps = get(),
            configService = get(),
            migration = get(),
            modVersion = get(),
            backupService = get(),
            validationService = get()
        )
    }

    single<ProjectsConfigService> {
        val paths: ConfigPaths = get()
        ProjectsConfigService(
            projectsPath = paths.root.resolve("projects.json"),
            backupDir = paths.root.resolve("backups"),
            fileOps = get(),
            fileWatcher = get(),
            backupService = get()
        )
    }

    single { ProjectConfigService(get(), get()) }
    single<LayoutPersistence> { LayoutConfigService(get(), get()) }
    single { TreeConfigService(get()) }
    single<Settings> { SettingsService(get(), get()) }
    single<MainStage> { MainStageService(get(), get(), get(), get()) }
    single<OverlayStage> { OverlayStageService(get(), get(), get(), get(), get(), get()) }
    single<FileTreePersistence> { FileTreePersistenceService(get(), get()) }
    single<FileTree> { FileTreeService(get()) }
    single<ProjectRuntime> { ProjectRuntimeService(get(), get()) }
    single<Styling> { StylingService() }
    single<EditorSession> { EditorSessionService(get(), get(), get()) }
    single<RecentProjects> { RecentProjectsService(get()) }
    single<WorkspaceLifecycle> { WorkspaceLifecycleService(get(), get(), get(), get(), get(), get(), get()) }

    single {
        ViewFactory(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            SettingsBootstrap.settingsRegistry,
            get(),
            get()
        )
    }
}
