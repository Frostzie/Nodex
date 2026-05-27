package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.bootstrap.koin.configModule
import io.github.frostzie.nodex.bootstrap.koin.coreModule
import io.github.frostzie.nodex.bootstrap.koin.fileModule
import io.github.frostzie.nodex.bootstrap.koin.navigationModule
import io.github.frostzie.nodex.bootstrap.koin.workspaceModule

import io.github.frostzie.nodex.api.misc.Styling
import io.github.frostzie.nodex.services.ui.StylingService
import io.github.frostzie.nodex.ui.ViewFactory
import org.koin.dsl.module

val appModule = module {
    includes(
        coreModule,
        configModule,
        fileModule,
        workspaceModule,
        navigationModule
    )

    single<Styling> { StylingService() }

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
