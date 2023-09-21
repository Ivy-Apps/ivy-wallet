package com.ivy.design.l0_system

import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val White = Color(0xFFFAFAFA)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Black = Color(0xFF111114)

// Primary
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Ivy = Color(0xFF6B4DFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple = Color(0xFF6B4DFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple1 = Color(0xFFC34CFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple2 = Color(0xFFFF4CFF)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue = Color(0xFF4CC3FF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue2 = Color(0xFF45E6E6)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue3 = Color(0xFF457BE6)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green = Color(0xFF14CC9E)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green2 = Color(0xFF45E67B)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green3 = Color(0xFF96E645)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green4 = Color(0xFFC7E62E)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Yellow = Color(0xFFFFEE33)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange = Color(0xFFF29F30)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange2 = Color(0xFFE67B45)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange3 = Color(0xFFFFC34C)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red = Color(0xFFFF4060)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red2 = Color(0xFFE62E2E)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red3 = Color(0xFFFF4CA6)

// Light
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val IvyLight = Color(0xFFD5CCFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple1Light = Color(0xFFEECCFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple2Light = Color(0xFFFFBFFF)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val BlueLight = Color(0xFFB3E6FF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue2Light = Color(0xFFB3FFFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue3Light = Color(0xFFCCDDFF)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GreenLight = Color(0xFFAAF2E0)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green2Light = Color(0xFF99FFBB)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green3Light = Color(0xFFCCFF99)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green4Light = Color(0xFFEEFF99)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val YellowLight = Color(0xFFFFF799)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val OrangeLight = Color(0xFFFFDEB3)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange2Light = Color(0xFFFFCCB3)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange3Light = Color(0xFFFFDC99)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val RedLight = Color(0xFFFFCCD5)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red2Light = Color(0xFFFFB3B3)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red3Light = Color(0xFFFFCCE6)

// Dark
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val IvyDark = Color(0xFF352680)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple1Dark = Color(0xFF622680)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple2Dark = Color(0xFF802680)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val BlueDark = Color(0xFF266280)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue2Dark = Color(0xFF227373)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue3Dark = Color(0xFF223D73)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GreenDark = Color(0xFF0A664F)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green2Dark = Color(0xFF22733D)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green3Dark = Color(0xFF66804D)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green4Dark = Color(0xFF637317)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val YellowDark = Color(0xFF807719)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val OrangeDark = Color(0xFF734B17)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange2Dark = Color(0xFF66371F)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange3Dark = Color(0xFF806226)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val RedDark = Color(0xFF801919)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red2Dark = Color(0xFF802030)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red3Dark = Color(0xFF802653)
// --------------------------------------------------------------------------------------------------

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val MediumBlack = Color(0xFF2B2C2D)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Gray = Color(0xFF939199)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val MediumWhite = Color(0xFFEFEEF0)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Transparent = Color(0x00000000)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GradientGreen = Gradient(Green, Color(0xFF49F2C8))

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Immutable
data class Gradient(
    val startColor: Color,
    val endColor: Color
) {
    companion object {
        @Deprecated("Old design system. Use `:ivy-design` and Material3")
        fun from(startColor: Int, endColor: Int?) = Gradient(
            startColor = startColor.toComposeColor(),
            endColor = (endColor ?: startColor).toComposeColor()
        )

        @Deprecated("Old design system. Use `:ivy-design` and Material3")
        fun solid(color: Color) = Gradient(color, color)

        @Deprecated("Old design system. Use `:ivy-design` and Material3")
        @Composable
        fun black() = Gradient(UI.colors.gray, UI.colors.pureInverse)
    }

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun asHorizontalBrush() = Brush.horizontalGradient(colors = listOf(startColor, endColor))
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun findContrastTextColor(backgroundColor: Color): Color {
    return if (isDarkColor(backgroundColor.toArgb())) White else Black
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun isDarkColor(color: Color): Boolean {
    return isDarkColor(color.toArgb())
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun isDarkColor(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) <= 0.5
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.asBrush(): Brush {
    return Brush.horizontalGradient(listOf(this, this))
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.dynamicContrast(): Color {
    val pickedColor = this.toHSVSpec()

    return when {
        pickedColor.s >= 0.5f && pickedColor.v >= 0.4f -> {
            // Primary
            if (isDarkColor(this)) {
                lighten()
            } else {
                darken()
            }
        }
        pickedColor.s <= 0.5f && pickedColor.v >= 0.8f -> {
            // Light
            darken()
        }
        pickedColor.s >= 0.1f && pickedColor.v <= 0.6f -> {
            // Dark
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

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.lighten(): Color {
    return this.hsv(
        s = 0.3f,
        v = 1f
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.darken(): Color {
    return this.hsv(
        s = 0.6f,
        v = 0.5f
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.toHSVSpec(): HSVSpec {
    val hsv = FloatArray(3)
    val color: Int = this.toArgb()
    android.graphics.Color.colorToHSV(color, hsv)
    return HSVSpec(hsv[0], hsv[1], hsv[2])
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
data class HSVSpec(
    val h: Float,
    val s: Float,
    val v: Float
)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Color.hsv(
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

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Int.toComposeColor() = Color(this)
