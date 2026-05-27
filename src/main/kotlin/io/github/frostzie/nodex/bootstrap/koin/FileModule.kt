package io.github.frostzie.nodex.bootstrap.koin

import io.github.frostzie.nodex.api.config.FileTreePersistence
import io.github.frostzie.nodex.api.file.FileOperations
import io.github.frostzie.nodex.api.file.FileTree
import io.github.frostzie.nodex.api.file.FileWatcher
import io.github.frostzie.nodex.services.core.FileService
import io.github.frostzie.nodex.services.files.FileTreePersistenceService
import io.github.frostzie.nodex.services.files.FileTreeService
import io.github.frostzie.nodex.services.files.FileWatcherService
import org.koin.dsl.module

val fileModule = module {
    single<FileWatcher> { FileWatcherService(get(), get()) }
    single<FileOperations> { FileService(get()) }
    // Not sure if this is the best place but here for now at least (prob moving to UiModule when more ui is added)
    single<FileTreePersistence> { FileTreePersistenceService(get(), get()) }
    single<FileTree> { FileTreeService(get()) }
}
