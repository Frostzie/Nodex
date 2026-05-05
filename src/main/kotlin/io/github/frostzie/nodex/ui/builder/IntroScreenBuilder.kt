package io.github.frostzie.nodex.ui.builder

import io.github.frostzie.nodex.api.config.Config
import io.github.frostzie.nodex.api.navigation.Navigation
import io.github.frostzie.nodex.ui.view.intro.IntroView
import io.github.frostzie.nodex.ui.view.layout.IntroLayoutView
import io.github.frostzie.nodex.ui.viewmodel.intro.IntroViewModel

/**
 * Builds the Intro screen layout.
 */
class IntroScreenBuilder(
    private val navigationService: Navigation,
    private val configService: Config
) {

    fun build(): IntroLayoutView {
        val introViewModel = IntroViewModel(navigationService, configService)
        val introView = IntroView(introViewModel)

        return IntroLayoutView(introView)
    }
}
