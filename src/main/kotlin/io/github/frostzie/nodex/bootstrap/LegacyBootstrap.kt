package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.settings.SettingsLoader
import io.github.frostzie.nodex.utils.LoggerProvider

@Deprecated("Only initializes Settings to keep config saving removed when settings reworked.")
object LegacyBootstrap {
    private val logger = LoggerProvider.getLogger("LegacyBootstrap")

    fun start() {
        logger.info("Initializing legacy systems...")
        SettingsLoader.initialize()
    }
}