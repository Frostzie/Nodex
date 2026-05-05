package io.github.frostzie.nodex.ui.view.layout

import io.github.frostzie.nodex.ui.view.intro.IntroView
import javafx.scene.layout.BorderPane

/**
 * The Intro Layout View.
 */
class IntroLayoutView(
    introView: IntroView
) : BorderPane() {

    init {
        center = introView
    }
}