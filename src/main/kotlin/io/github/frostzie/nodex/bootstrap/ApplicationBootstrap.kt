package io.github.frostzie.nodex.bootstrap

object ApplicationBootstrap {
    fun start() {
        PlatformBootstrap.start()
        ServiceBootstrap.start()
        MinecraftBootstrap.start()
        LegacyBootstrap.start()
        UiBootstrap.start()
    }
}