package io.github.frostzie.nodex.bootstrap

import io.github.frostzie.nodex.ingame.DefaultCommands
import io.github.frostzie.nodex.loader.fabric.Keybinds
import io.github.frostzie.nodex.modules.universal.UniversalWorldIntegration

/**
 * Responsible for initializing Minecraft/Fabric integration.
 */
//TODO: Double check if it meets all the new req.
object MinecraftBootstrap {
    fun start() {
        DefaultCommands.registerCommands()
        Keybinds.register()
        // UniversalWorldIntegration.initialize()
    }
}