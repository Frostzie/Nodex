package io.github.frostzie.nodex.bootstrap

object ApplicationBootstrap {
    fun start() {
        PlatformBootstrap.start()
        SettingsBootstrap.start()
        ServiceBootstrap.start()
        MinecraftBootstrap.start()
        UiBootstrap.start()
    }

    fun stop() {
        ServiceBootstrap.stop()
    }
}