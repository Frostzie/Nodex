package io.github.frostzie.nodex.services.ui

import io.github.frostzie.nodex.api.navigation.FocusTracker
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.api.navigation.Layout
import io.github.frostzie.nodex.api.navigation.MainStage
import io.github.frostzie.nodex.api.navigation.WindowProfile
import io.github.frostzie.nodex.ui.utils.WindowGeometryTracker
import io.github.frostzie.nodex.ui.utils.extensions.applyBasePolicy
import javafx.scene.Scene
import javafx.scene.layout.Region
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * Service responsible for managing the primary application window lifecycle.
 *
 * Handles the window lifecycle, geometry tracking.
 */
open class MainStageService(
    private val layoutService: Layout,
    private val navigationService: Navigation,
    private val focusService: FocusTracker,
    private val windowProfile: WindowProfile
) : MainStage {
    private var stage: Stage? = null
    private var content: Region? = null
    private var tracker: WindowGeometryTracker? = null

    /**
     * Initialize the service with the primary stage and content.
     */
    override fun initialize(primaryStage: Stage, content: Region, scene: Scene) {
        check(stage == null) { "MainStageService has already been initialized." }
        this.stage = primaryStage
        this.content = content

        primaryStage.initStyle(StageStyle.EXTENDED)
        scene.root = content
        primaryStage.scene = scene

        setupGeometryTracker(primaryStage)

        focusService.trackStage(primaryStage)
        setupScreenListeners()

        primaryStage.setOnCloseRequest { event ->
            event.consume()
            hide()
        }
    }

    override fun show() {
        val currentStage = stage ?: return
        val currentContent = content ?: return
        val screen = navigationService.currentScreen.get()

        if (!currentStage.isShowing) {
            applyPolicy(currentStage, currentContent, screen)
            currentStage.show()
        }

        if (currentStage.isIconified) {
            currentStage.isIconified = false
        }

        currentStage.toFront()
        currentStage.requestFocus()
    }

    private fun setupGeometryTracker(primaryStage: Stage) {
        tracker = WindowGeometryTracker(primaryStage) { newState ->
            val screen = navigationService.currentScreen.get()
            val profile = windowProfile.getScreenPolicy(screen)
            if (profile.isPersistent) {
                layoutService.updateWindowState(screen, newState)
            }
        }
    }

    private fun setupScreenListeners() {
        navigationService.currentScreen.addListener { _, _, newScreen ->
            val currentStage = stage ?: return@addListener
            val currentContent = content ?: return@addListener
            if (currentStage.isShowing) {
                applyPolicy(currentStage, currentContent, newScreen)
            }
        }
    }

    private fun applyPolicy(currentStage: Stage, content: Region, screen: AppScreen) {
        val policy = windowProfile.getScreenPolicy(screen)
        val state = layoutService.getWindowState(screen)

        // Shared logic via extension
        currentStage.applyBasePolicy(content, policy, state, tracker)
    }

    override fun hide() {
        stage?.hide()
    }

    override fun isShowing(): Boolean = stage?.isShowing ?: false
    override fun isIconified(): Boolean = stage?.isIconified ?: false
}
