package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.navigation.ToolWindowProvider
import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.domain.config.ToolWindowConfig
import io.github.frostzie.nodex.domain.uicontract.ToolWindowState
import io.github.frostzie.nodex.domain.uicontract.PanelPosition
import io.github.frostzie.nodex.domain.uicontract.ToolWindow
import io.github.frostzie.nodex.utils.LoggerProvider
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Service that owns and manages the collection of Tool Windows.
 */
class ToolWindowService(private val windowProfile: WindowProfile) : ToolWindowProvider {
    private val logger = LoggerProvider.getLogger("ToolWindowService")

    private val _states = FXCollections.observableArrayList<ToolWindowState>()
    override val states: ObservableList<ToolWindowState> get() = _states

    init {
        resetToDefaults()
    }

    private fun resetToDefaults() {
        _states.clear()
        windowProfile.getAllToolKinds().forEach { type ->
            val profile = windowProfile.getToolPolicy(type)
            _states.add(
                ToolWindowState(
                    toolType = type,
                    title = profile.title,
                    visible = profile.defaultVisible,
                    anchor = profile.defaultAnchor,
                    sizeRatio = profile.defaultSizeRatio
                )
            )
        }
    }

    override fun initializeFromConfig(configs: Map<String, ToolWindowConfig>) {
        windowProfile.getAllToolKinds().forEach { type ->
            val config = configs[type.name]
            val profile = windowProfile.getToolPolicy(type)

            val anchor = try {
                PanelPosition.valueOf(config?.anchor ?: profile.defaultAnchor.name)
            } catch (_: Exception) {
                profile.defaultAnchor
            }

            updateState(type) {
                it.copy(
                    visible = config?.visible ?: profile.defaultVisible,
                    anchor = anchor
                )
            }
        }
    }

    override fun createConfigs(): Map<String, ToolWindowConfig> {
        return _states.associate { state ->
            state.toolType.name to ToolWindowConfig(
                toolType = state.toolType.name,
                anchor = state.anchor.name,
                visible = state.visible
            )
        }
    }

    override fun setVisible(type: ToolWindow, visible: Boolean) {
        if (visible) {
            // If showing, hide others in the same slot
            val targetAnchor = _states.find { it.toolType == type }?.anchor ?: return
            hideOthersInSlot(type, targetAnchor)
        }
        logger.debug("Setting tool window {} visible: {}", type, visible)
        updateState(type) { it.copy(visible = visible) }
    }

    override fun setAnchor(type: ToolWindow, anchor: PanelPosition) {
        val state = _states.find { it.toolType == type } ?: return
        if (state.anchor == anchor) return // No change

        logger.debug("Setting tool window {} anchor: {}", type, anchor)
        hideOthersInSlot(type, anchor)
        updateState(type) { it.copy(anchor = anchor, visible = true) }
    }

    private fun hideOthersInSlot(excludeType: ToolWindow, anchor: PanelPosition) {
        _states.forEachIndexed { index, state ->
            if (state.toolType != excludeType && state.anchor == anchor && state.visible) {
                logger.debug("Auto-hiding conflicting tool {} at {}", state.toolType, anchor)
                _states[index] = state.copy(visible = false)
            }
        }
    }

    override fun setSizeRatio(type: ToolWindow, ratio: Double) {
        updateState(type) { it.copy(sizeRatio = ratio.coerceIn(0.0, 1.0)) }
    }

    private fun updateState(type: ToolWindow, transform: (ToolWindowState) -> ToolWindowState) {
        val index = _states.indexOfFirst { it.toolType == type }
        if (index != -1) {
            val old = _states[index]
            val next = transform(old)
            if (old != next) {
                _states[index] = next
            }
        }
    }
}
