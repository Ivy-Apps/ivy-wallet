package com.ivy.wallet.ui.theme

import androidx.annotation.ColorInt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.ivy.design.l0_system.UI
import com.ivy.legacy.utils.densityScope

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val White = Color(0xFFFAFAFA)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Black = Color(0xFF111114)

// Primary
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Ivy = Color(0xFF6B4DFF)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Blue = Color(0xFF4CC3FF)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Green = Color(0xFF14CC9E)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Orange = Color(0xFFF29F30)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red = Color(0xFFFF4060)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red3 = Color(0xFFFF4CA6)

// Light
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val IvyLight = Color(0xFFD5CCFF)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GreenLight = Color(0xFFAAF2E0)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val OrangeLight = Color(0xFFFFDEB3)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val RedLight = Color(0xFFFFCCD5)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Red3Light = Color(0xFFFFCCE6)

// Dark
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val IvyDark = Color(0xFF352680)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val Purple1Dark = Color(0xFF622680)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GreenDark = Color(0xFF0A664F)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val OrangeDark = Color(0xFF734B17)
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
val GradientRed = Gradient(Red, Color(0xFFFF99AB))
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GradientGreen = Gradient(Green, Color(0xFF49F2C8))
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GradientOrangeRevert = Gradient(Color(0xFFF2CD9E), Orange)
@Deprecated("Old design system. Use `:ivy-design` and Material3")
val GradientIvy = Gradient(Ivy, Color(0xFFAA99FF))

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.gradientCutBackgroundTop(
    endY: Dp = 32.dp
) = composed {
    background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Transparent,
                UI.colors.pure,
            ),
            endY = densityScope {
                endY.toPx()
            }
        )
    ).padding(top = 16.dp)
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun Modifier.gradientCutBackgroundBottom(
    paddingBottom: Dp,
) = composed {
    background(
        brush = Brush.verticalGradient(
            colors = listOf(
                UI.colors.pure,
                Transparent
            ),
        )
    ).padding(bottom = paddingBottom)
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun pureBlur() = UI.colors.pure.copy(alpha = 0.95f)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun mediumBlur() = UI.colors.medium.copy(alpha = 0.95f)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun gradientExpenses() = Gradient(UI.colors.pureInverse, UI.colors.gray)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
data class Gradient(
    val startColor: Color,
    val endColor: Color
) {
    companion object {
        @Deprecated("Old design system. Use `:ivy-design` and Material3")
        fun from(gradient: com.ivy.design.l0_system.Gradient) =
            Gradient(gradient.startColor, gradient.endColor)

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

    @Deprecated("Old design system. Use `:ivy-design` and Material3")
    fun asVerticalBrush() = Brush.verticalGradient(colors = listOf(startColor, endColor))
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
