package io.github.frostzie.nodex.ui.utils.extensions

import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter

/**
 * Sets the max length of text you can enter in a [TextField].
 */
fun TextField.withMaxLength(maxLength: Int): TextField {
    textFormatter = TextFormatter<String> { change ->
        if (change.controlNewText.length <= maxLength) change else null
    }
    return this
}