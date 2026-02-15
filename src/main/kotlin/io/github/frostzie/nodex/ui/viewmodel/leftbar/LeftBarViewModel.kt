package io.github.frostzie.nodex.ui.viewmodel.leftbar

import io.github.frostzie.nodex.services.core.LayoutService

class LeftBarViewModel(private val layoutService: LayoutService) {

    fun toggleSidebar() {
        layoutService.sidebarVisible.set(!layoutService.sidebarVisible.get())
    }
}