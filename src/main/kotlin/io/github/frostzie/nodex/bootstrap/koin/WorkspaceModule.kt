package io.github.frostzie.nodex.bootstrap.koin

import io.github.frostzie.nodex.api.config.RecentProjects
import io.github.frostzie.nodex.api.workspace.EditorSession
import io.github.frostzie.nodex.api.workspace.ProjectRuntime
import io.github.frostzie.nodex.api.workspace.WorkspaceLifecycle
import io.github.frostzie.nodex.services.config.global.RecentProjectsService
import io.github.frostzie.nodex.services.workspace.EditorSessionService
import io.github.frostzie.nodex.services.workspace.ProjectRuntimeService
import io.github.frostzie.nodex.services.workspace.WorkspaceLifecycleService
import org.koin.dsl.module

val workspaceModule = module {
    single<ProjectRuntime> { ProjectRuntimeService(get(), get()) }
    single<EditorSession> { EditorSessionService(get(), get(), get()) }
    single<RecentProjects> { RecentProjectsService(get()) }
    single<WorkspaceLifecycle> { WorkspaceLifecycleService(get(), get(), get(), get(), get(), get(), get()) }

}
