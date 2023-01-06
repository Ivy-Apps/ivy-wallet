package com.ivy.design.l0_system.color

import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

// region Contrast color
@Composable
fun rememberContrast(color: Color): Color = remember(color) {
    contrastColor(color)
}


fun contrastColor(color: Color): Color = if (isDarkColor(color.toArgb())) White else Black
// endregion

// region Dynamic Contrast
@Composable
fun rememberDynamicContrast(color: Color): Color = remember(color) {
    color.dynamicContrast()
}

private fun Color.dynamicContrast(): Color {
    val pickedColor = this.toHSVSpec()

    return when {
        pickedColor.s >= 0.5f && pickedColor.v >= 0.4f -> {
            //Primary
            if (isDarkColor(this)) {
                lighten()
            } else {
                darken()
            }
        }
        pickedColor.s <= 0.5f && pickedColor.v >= 0.8f -> {
            //Light
            darken()
        }
        pickedColor.s >= 0.1f && pickedColor.v <= 0.6f -> {
            //Dark
            lighten()
        }
        else -> {
            if (isDarkColor(this)) {
                lighten()
            } else {
                darken()
            }
        }
    }
}

private fun Color.lighten(): Color = this.hsv(
    s = 0.3f,
    v = 1f
)

private fun Color.darken(): Color = this.hsv(
    s = 0.6f,
    v = 0.5f
)

private fun Color.toHSVSpec(): HSVSpec {
    val hsv = FloatArray(3)
    val color: Int = this.toArgb()
    android.graphics.Color.colorToHSV(color, hsv)
    return HSVSpec(hsv[0], hsv[1], hsv[2])
}

data class HSVSpec(
    val h: Float,
    val s: Float,
    val v: Float
)

private fun Color.hsv(
    h: Float? = null,
    s: Float,
    v: Float
): Color {
    val hsv = FloatArray(3)
    val color: Int = this.toArgb()
    android.graphics.Color.colorToHSV(color, hsv)

    if (h != null) {
        hsv[0] = h
    }

    hsv[1] = s
    hsv[2] = v

    return Color(android.graphics.Color.HSVToColor(hsv))
}
// endregion

private fun isDarkColor(color: Color): Boolean = isDarkColor(color.toArgb())

private fun isDarkColor(@ColorInt color: Int): Boolean =
    ColorUtils.calculateLuminance(color) <= 0.5

// region Extensions
fun Int.toComposeColor() = Color(this)

fun Color.asBrush(): Brush = SolidColor(this)
// endregion

// region Hex <> Color
fun Color.toHex() = Integer.toHexString(this.toArgb()).drop(2).uppercase()

fun fromHex(hex: String): Color? = try {
    android.graphics.Color.parseColor("#$hex").toComposeColor()
} catch (ignored: Exception) {
    null
}
// endregion