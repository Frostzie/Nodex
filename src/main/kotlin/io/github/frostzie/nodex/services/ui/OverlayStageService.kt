package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.misc.Styling
import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.api.navigation.OverlayStage
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.ui.ViewFactory
import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.ui.utils.WindowGeometryTracker
import io.github.frostzie.nodex.ui.utils.extensions.applyBasePolicy
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * Service responsible for managing modal app windows (Overlays).
 *
 * It observes the [NavigationService] and creates/shows/hides separate
 * JavaFX Stages based on the active [OverlayScreen].
 */
open class OverlayStageService(
    private val layoutService: Layout,
    private val navigationService: Navigation,
    private val focusService: FocusTracker,
    private val stylingService: Styling,
    private val viewFactory: ViewFactory,
    private val windowProfile: WindowProfile
) : OverlayStage {
    private val activeStages = mutableMapOf<OverlayScreen, Stage>()
    private var primaryWindowStage: Stage? = null

    /**
     * Sets the primary window stage to be used as the owner for modals.
     */
    override fun setPrimaryStage(stage: Stage) {
        this.primaryWindowStage = stage
    }

    /**
     * Initializes the service by observing overlay changes.
     */
    override fun initialize() {
        setupNavigationListeners()
    }

    private fun setupNavigationListeners() {
        navigationService.activeOverlay.addListener { _, _, newOverlay ->
            if (newOverlay != null) {
                showOverlay(newOverlay)
            } else {
                closeAllOverlays()
            }
        }
    }

    private fun showOverlay(overlay: OverlayScreen) {
        if (activeStages.containsKey(overlay)) {
            activeStages[overlay]?.toFront()
            return
        }

        val stage = Stage()
        val rootNode = viewFactory.createOverlayContent(overlay)
        val scene = Scene(rootNode)

        scene.stylesheets.addAll(stylingService.getStylesheetUrls())
        scene.fill = Color.valueOf("#1c2128") //TODO: Replace with dynamic color from themes

        stage.initStyle(StageStyle.EXTENDED)
        scene.root = rootNode
        stage.scene = scene

        val tracker = setupGeometryTracker(stage, overlay)
        applyPolicy(stage, rootNode, overlay, tracker)

        stage.setOnCloseRequest { event ->
            event.consume()
            navigationService.closeOverlay()
        }

        activeStages[overlay] = stage
        stage.show()
        focusService.trackStage(stage)
    }

    private fun setupGeometryTracker(stage: Stage, overlay: OverlayScreen): WindowGeometryTracker {
        return WindowGeometryTracker(stage) { newState ->
            val profile = windowProfile.getOverlayPolicy(overlay)
            if (profile.isPersistent) {
                layoutService.updateOverlayWindowState(overlay, newState)
            }
        }
    }

    private fun applyPolicy(
        stage: Stage,
        rootNode: Region,
        overlay: OverlayScreen,
        tracker: WindowGeometryTracker
    ) {
        val policy = windowProfile.getOverlayPolicy(overlay)
        val state = layoutService.getOverlayWindowState(overlay)

        // Overlay specific settings
        primaryWindowStage?.let { stage.initOwner(it) }
        if (policy.isModal) {
            stage.initModality(Modality.APPLICATION_MODAL)
        }
        stage.isAlwaysOnTop = policy.alwaysOnTop

        // Shared logic via extension
        stage.applyBasePolicy(rootNode, policy, state, tracker)
    }

    private fun closeAllOverlays() {
        activeStages.values.forEach { it.close() }
        activeStages.clear()
    }
}
