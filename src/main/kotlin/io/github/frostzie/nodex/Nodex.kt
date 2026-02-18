package io.github.frostzie.nodex

import io.github.frostzie.nodex.bootstrap.ApplicationBootstrap
import net.fabricmc.api.ModInitializer

class Nodex : ModInitializer {

    override fun onInitialize() {
        ApplicationBootstrap.start()
    }
}