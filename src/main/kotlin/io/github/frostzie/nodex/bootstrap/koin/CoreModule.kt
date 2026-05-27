package io.github.frostzie.nodex.bootstrap.koin

import io.github.frostzie.nodex.api.concurrency.Concurrency
import io.github.frostzie.nodex.api.misc.ModVersion
import io.github.frostzie.nodex.api.misc.PerformanceMonitor
import io.github.frostzie.nodex.services.core.ConcurrencyService
import io.github.frostzie.nodex.services.core.ModVersionService
import io.github.frostzie.nodex.services.core.PerformanceService
import org.koin.dsl.module

val coreModule = module {
    single<ModVersion> { ModVersionService() }
    single<Concurrency> { ConcurrencyService() }
    single<PerformanceMonitor> { PerformanceService() }
}
