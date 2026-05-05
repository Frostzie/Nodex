package io.github.frostzie.nodex.domain.registry

import io.github.frostzie.nodex.domain.uicontract.AppScreen
import io.github.frostzie.nodex.domain.uicontract.OverlayScreen
import io.github.frostzie.nodex.domain.uicontract.WindowPolicy

/**
 * Static metadata for all screen profiles.
 */
object ScreenProfiles {

    val appScreens: Map<AppScreen, WindowPolicy> = mapOf(
        AppScreen.INTRO to WindowPolicy(
            title = "Nodex - Welcome",
            prefWidth = 825.0,
            prefHeight = 750.0,
            isResizable = false,
            isPersistent = false
        ),
        AppScreen.PROJECT_MANAGER to WindowPolicy(
            title = "Nodex - Project Manager",
            prefWidth = 825.0,
            prefHeight = 750.0,
            isResizable = false,
            isPersistent = false,
            headerButtonHeight = 40.0
        ),
        AppScreen.IDE to WindowPolicy(
            title = "Nodex",
            headerButtonHeight = 35.0
        )
    )

    val overlayScreens: Map<OverlayScreen, WindowPolicy> = mapOf(
        OverlayScreen.SETTINGS to WindowPolicy(
            title = "Settings",
            prefWidth = 950.0,
            prefHeight = 750.0,
            isModal = true,
            isResizable = true,
            headerButtonHeight = 45.0
        )
    )
}
