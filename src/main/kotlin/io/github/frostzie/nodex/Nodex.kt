package io.github.frostzie.nodex

import io.github.frostzie.nodex.bootstrap.ApplicationBootstrap
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

class Nodex : ModInitializer {

    override fun onInitialize() {
        System.setProperty("javafx.enablePreview", "true") // Enable HeaderBar
        System.setProperty("javafx.suppressPreviewWarning", "true")
        ApplicationBootstrap.start()
        
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            ApplicationBootstrap.stop()
        }
    }
}