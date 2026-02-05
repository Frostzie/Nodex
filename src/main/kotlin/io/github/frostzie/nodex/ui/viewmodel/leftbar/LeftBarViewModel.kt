package io.github.frostzie.nodex.ui.viewmodel.leftbar

import io.github.frostzie.nodex.services.core.LayoutService

class LeftBarViewModel {

    fun toggleSidebar() {
        LayoutService.sidebarVisible.set(!LayoutService.sidebarVisible.get())
    }
}