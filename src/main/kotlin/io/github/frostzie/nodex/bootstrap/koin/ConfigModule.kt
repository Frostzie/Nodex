package io.github.frostzie.nodex.bootstrap.koin

import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.config.LayoutPersistence
import io.github.frostzie.nodex.api.config.Migration
import io.github.frostzie.nodex.api.settings.Settings
import io.github.frostzie.nodex.bootstrap.SettingsBootstrap
import io.github.frostzie.nodex.domain.config.ConfigPaths
import io.github.frostzie.nodex.loader.fabric.Folders
import io.github.frostzie.nodex.services.config.BackupService
import io.github.frostzie.nodex.services.config.ConfigLocationService
import io.github.frostzie.nodex.services.config.ConfigMoveService
import io.github.frostzie.nodex.services.config.MigrationService
import io.github.frostzie.nodex.services.config.global.ProjectsConfigService
import io.github.frostzie.nodex.services.config.global.SettingsConfigService
import io.github.frostzie.nodex.services.config.project.LayoutConfigService
import io.github.frostzie.nodex.services.config.project.ProjectConfigService
import io.github.frostzie.nodex.services.config.project.TreeConfigService
import io.github.frostzie.nodex.services.config.stationary.ConfigService
import io.github.frostzie.nodex.services.settings.SettingsService
import io.github.frostzie.nodex.services.settings.SettingsValidationService
import org.koin.dsl.module

val configModule = module {
    single<Migration> { MigrationService() }
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

    single<Settings> { SettingsService(get(), get()) }

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
}
