package io.github.frostzie.nodex.loader.fabric

import com.mojang.blaze3d.platform.InputConstants
import io.github.frostzie.nodex.bootstrap.UiBootstrap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW

//TODO: Wrap keybind building and move to ingame dir
object Keybinds {
    private var toggleIDEKey: KeyMapping? = null

    fun register() {
        //? if <=1.21.8 {
        /*toggleIDEKey = KeyMappingHelper.registerKeyMapping(KeyMapping(
            "key.nodex.toggle_ide",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.nodex.general"
        ))
        *///?} else {
        toggleIDEKey = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.nodex.toggle_ide",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("nodex", "general"))
        ))
        //?}

        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            while (toggleIDEKey?.consumeClick() == true) {
                UiBootstrap.showAndFocusWindow()
            }
        }
    }
}