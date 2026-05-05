package io.github.frostzie.nodex.ui.viewmodel.ide.leftbar

import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.domain.uicontract.ToolWindow

class LeftBarViewModel(private val layoutService: Layout) {

    fun toggleFileTree() {
        val toolWindowManager = layoutService.toolWindowProvider
        val filesState = toolWindowManager.states.find { it.toolType == ToolWindow.FILES } ?: return
        toolWindowManager.setVisible(ToolWindow.FILES, !filesState.visible)
    }
}
