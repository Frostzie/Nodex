package io.github.frostzie.nodex.loader.fabric

import net.fabricmc.loader.api.FabricLoader

object ModVersion {
    fun getModVersion(): String =
        FabricLoader.getInstance()
            .getModContainer("nodex")
            .map { it.metadata.version.friendlyString }
            .orElse("unknown")
}