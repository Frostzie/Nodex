package io.github.frostzie.nodex.bootstrap.koin

import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.navigation.OverlayStage
import io.github.frostzie.nodex.api.navigation.ToolWindowProvider
import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.services.core.LayoutService
import io.github.frostzie.nodex.services.ui.FocusService
import io.github.frostzie.nodex.services.ui.MainStageService
import io.github.frostzie.nodex.services.ui.NavigationService
import io.github.frostzie.nodex.services.ui.OverlayStageService
import io.github.frostzie.nodex.services.ui.ToolWindowService
import io.github.frostzie.nodex.services.ui.WindowProfileService
import org.koin.dsl.module

val navigationModule = module {
    single<FocusTracker> { FocusService() }
    single<Navigation> { NavigationService(get()) }
    single<WindowProfile> { WindowProfileService() }
    single<ToolWindowProvider> { ToolWindowService(get()) }
    single<Layout> { LayoutService(get(), get(), get()) }
    single<MainStage> { MainStageService(get(), get(), get(), get()) }
    single<OverlayStage> { OverlayStageService(get(), get(), get(), get(), get(), get()) }
}
