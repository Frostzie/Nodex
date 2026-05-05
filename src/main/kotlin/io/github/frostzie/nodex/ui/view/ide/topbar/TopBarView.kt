package io.github.frostzie.nodex.ui.view.ide.topbar

import atlantafx.base.theme.Styles
import io.github.frostzie.nodex.ui.viewmodel.ide.topbar.TopBarViewModel
import javafx.scene.control.Button
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.HeaderBar
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon

/**
 * The TopBar of the IDE, containing action bar and window controls.
 */
class TopBarView(private val viewModel: TopBarViewModel) : HeaderBar() {
    private val menuBar = createMenuBar()
    private val settingsBtn = createSettingsBtn()

    init {
        prefHeight = 35.0
        minHeight = 35.0
        maxHeight = 35.0
        styleClass.add("top-bar")

        left = menuBar
        right = settingsBtn
    }

    private fun createMenuBar(): MenuBar {
        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: transparent;" //TODO: Remove and fix Menubar height
        menuBar.menus.addAll(
            Menu(
                "File", null,
                MenuItem("Close").apply { setOnAction { viewModel.closeApp() } }
            ),
            Menu(
                "View", null,
                Menu(
                    "Screen Switching", null, //TODO: Remove screen switching only here for dev
                    MenuItem("Intro").apply { setOnAction { viewModel.openIntro() } },
                    MenuItem("ProjectManager").apply { setOnAction { viewModel.openProjectManager() } },
                    MenuItem("Settings").apply { setOnAction { viewModel.openSettings() } }
                )
            )
        )
        return menuBar
    }

    private fun createSettingsBtn(): Button {
        val settingsBtn = Button(null, FontIcon(Feather.SETTINGS))
        settingsBtn.styleClass.addAll(
            Styles.FLAT,
            Styles.BUTTON_ICON
        )
        settingsBtn.setOnAction { viewModel.openSettings() }

        return settingsBtn
    }
}
