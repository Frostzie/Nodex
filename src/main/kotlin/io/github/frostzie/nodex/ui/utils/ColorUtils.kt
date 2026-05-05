package io.github.frostzie.nodex.ui.utils

import io.github.frostzie.nodex.domain.entity.RgbaColor
import javafx.scene.paint.Color

/**
 * Color conversion utility.
 *
 * Bridge between JavaFX [Color] and domain [RgbaColor].
 */
object ColorUtils {

    /**
     * Converts a JavaFX [Color] to a domain [RgbaColor].
     *
     * RGB channels are scaled from 0.0–1.0 to 0–255 and rounded.
     * Alpha is used directly as the `a` component (0.0–1.0).
     */
    fun colorToRgba(color: Color): RgbaColor = RgbaColor(
        r = (color.red * 255).toInt().coerceIn(0, 255),
        g = (color.green * 255).toInt().coerceIn(0, 255),
        b = (color.blue * 255).toInt().coerceIn(0, 255),
        a = color.opacity
    )

    /**
     * Converts a domain [RgbaColor] to a JavaFX [Color].
     */
    fun rgbaToColor(rgba: RgbaColor): Color = Color.rgb(rgba.r, rgba.g, rgba.b, rgba.a)
}
