package io.github.frostzie.nodex.api.workspace

import io.github.frostzie.nodex.domain.uicontract.AppScreen
import java.nio.file.Path

/**
 * Handles workspace-level lifecycle flows:
 * startup routing, project open/close, and shutdown cleanup.
 *
 * @see io.github.frostzie.nodex.services.workspace.WorkspaceLifecycleService
 */
interface WorkspaceLifecycle {
    /**
     * Resolves which primary screen should be shown at startup.
     */
    fun resolveStartupScreen(): AppScreen

    /**
     * Opens a project path as the active workspace.
     *
     * @return true if the project was opened.
     */
    fun openProject(path: Path): Boolean

    /**
     * Closes the currently active workspace session.
     */
    fun closeCurrentProject()

    /**
     * Flushes pending runtime state before app shutdown.
     */
    fun shutdown()
}
