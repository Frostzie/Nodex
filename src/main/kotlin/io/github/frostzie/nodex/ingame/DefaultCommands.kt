package io.github.frostzie.nodex.ingame

import io.github.frostzie.nodex.bootstrap.UiBootstrap
import io.github.frostzie.nodex.loader.fabric.CommandRegistration

object DefaultCommands {
    fun registerCommands() {
        CommandRegistration.register("nodex") {
            executes {
                UiBootstrap.toggleWindow()
                1
            }

            // Mirrors current project to world. //TODO: Support any universal project mirroring
            /* TODO: Re-add Config
            literal("internal") {
                literal("mirror_current") {
                    executes {
                        if (!MainConfig.universalFolderToggle.get() || !MainConfig.universalDatapackToggle.get()) return@executes 0

                        val root = WorkspaceManager.currentWorkspaceRoot
                        if (root != null && UniversalPackManager.isUniversalProject(root)) {
                            if (UniversalPackManager.mirrorToWorld(root)) {
                                // Execute reload to apply changes
                                MCInterface.sendCommand("reload")
                            }
                         }
                        1
                    }
                }
            }*/
        }
    }
}