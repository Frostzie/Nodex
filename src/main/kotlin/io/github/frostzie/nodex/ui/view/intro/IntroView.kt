package io.github.frostzie.nodex.ui.view.intro

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import io.github.frostzie.nodex.domain.uicontract.Links
import io.github.frostzie.nodex.ui.utils.HyperlinkUtils
import io.github.frostzie.nodex.ui.viewmodel.intro.IntroViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

/**
 * The Intro screen. Default screen if unsaved configs.
 */
class IntroView(
    private val viewModel: IntroViewModel
) : VBox(40.0) {

    init {
        alignment = Pos.CENTER
        padding = Insets(30.0, 40.0, 40.0, 40.0)

        val header = VBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("Welcome to Nodex").apply {
                    styleClass.add(Styles.TITLE_1)
                },
                Label("The integrated coding environment for datapack creators!").apply {
                    styleClass.add(Styles.TEXT_MUTED)
                }
            )
        }

        val mediaBox = HBox().apply {
            padding = Insets(0.0, 0.0, 00.0, 40.0) // To align to the middle a bit better
            maxWidth = 800.0
            prefHeight = 400.0
            val img = Image("assets/nodex/png/IntroScreenAlpha.png", 800.0, 400.0, true, true)
            val imgView = ImageView(img)
            children.add(imgView)
        }

        //TODO: Make a tutorial
        val tutorialStartBtn = Button("Tutorial").apply {
            prefWidth = 150.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            setOnAction { viewModel.tutorialStart() }
            isDisable = true
        }

        val tutorialSkipBtn = Button("Skip").apply {
            prefWidth = 150.0
            prefHeight = 40.0
            styleClass.add(Styles.LARGE)
            setOnAction { viewModel.tutorialSkip() }
            isFocusTraversable = false
        }

        val actionButtons = HBox().apply {
            maxWidth = 800.0
            alignment = Pos.CENTER
            children.addAll(
                tutorialStartBtn,
                Spacer(),
                tutorialSkipBtn
            )
        }

        val footerLinks = HBox().apply {
            alignment = Pos.CENTER
            children.addAll(
                HyperlinkUtils.create("Discord", Links.DISCORD),
                Spacer(),
                HyperlinkUtils.create("Github", Links.GITHUB),
                Spacer(),
                HyperlinkUtils.create("Modrinth", Links.MODRINTH),
                Spacer(),
                HyperlinkUtils.create("Donate", Links.DONATE),
                Spacer(),
                HyperlinkUtils.create("Change logs", Links.CHANGELOGS),
                //Spacer(),
                //HyperlinkUtils.create("Wiki", Links.WIKI), //TODO: Eventually
            )
        }

        val footerContainer = VBox().apply {
            alignment = Pos.BOTTOM_CENTER
            setVgrow(this, Priority.ALWAYS)
            children.add(footerLinks)
        }

        children.addAll(header, mediaBox, actionButtons, footerContainer)
    }
}