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
import com.ivy.wallet.utils.densityScope


val White = Color(0xFFFAFAFA)
val Black = Color(0xFF111114)

//Primary
val Ivy = Color(0xFF6B4DFF)
val Purple1 = Color(0xFFC34CFF)
val Purple2 = Color(0xFFFF4CFF)

val Blue = Color(0xFF4CC3FF)
val Blue2 = Color(0xFF45E6E6)
val Blue3 = Color(0xFF457BE6)

val Green = Color(0xFF14CC9E)
val Green2 = Color(0xFF45E67B)
val Green3 = Color(0xFF96E645)
val Green4 = Color(0xFFC7E62E)

val Yellow = Color(0xFFFFEE33)

val Orange = Color(0xFFF29F30)
val Orange2 = Color(0xFFE67B45)
val Orange3 = Color(0xFFFFC34C)

val Red = Color(0xFFFF4060)
val Red2 = Color(0xFFE62E2E)
val Red3 = Color(0xFFFF4CA6)


//Light
val IvyLight = Color(0xFFD5CCFF)
val Purple1Light = Color(0xFFEECCFF)
val Purple2Light = Color(0xFFFFBFFF)

val BlueLight = Color(0xFFB3E6FF)
val Blue2Light = Color(0xFFB3FFFF)
val Blue3Light = Color(0xFFCCDDFF)

val GreenLight = Color(0xFFAAF2E0)
val Green2Light = Color(0xFF99FFBB)
val Green3Light = Color(0xFFCCFF99)
val Green4Light = Color(0xFFEEFF99)

val YellowLight = Color(0xFFFFF799)

val OrangeLight = Color(0xFFFFDEB3)
val Orange2Light = Color(0xFFFFCCB3)
val Orange3Light = Color(0xFFFFDC99)

val RedLight = Color(0xFFFFCCD5)
val Red2Light = Color(0xFFFFB3B3)
val Red3Light = Color(0xFFFFCCE6)


//Dark
val IvyDark = Color(0xFF352680)
val Purple1Dark = Color(0xFF622680)
val Purple2Dark = Color(0xFF802680)

val BlueDark = Color(0xFF266280)
val Blue2Dark = Color(0xFF227373)
val Blue3Dark = Color(0xFF223D73)

val GreenDark = Color(0xFF0A664F)
val Green2Dark = Color(0xFF22733D)
val Green3Dark = Color(0xFF66804D)
val Green4Dark = Color(0xFF637317)

val YellowDark = Color(0xFF807719)

val OrangeDark = Color(0xFF734B17)
val Orange2Dark = Color(0xFF66371F)
val Orange3Dark = Color(0xFF806226)

val RedDark = Color(0xFF801919)
val Red2Dark = Color(0xFF802030)
val Red3Dark = Color(0xFF802653)
//--------------------------------------------------------------------------------------------------


val MediumBlack = Color(0xFF2B2C2D)
val Gray = Color(0xFF939199)
val MediumWhite = Color(0xFFEFEEF0)


val Transparent = Color(0x00000000)

val GradientRed = Gradient(Red, Color(0xFFFF99AB))
val GradientGreen = Gradient(Green, Color(0xFF49F2C8))
val GradientOrange = Gradient(Orange, OrangeLight)
val GradientOrangeDark = Gradient(OrangeDark, Color(0xFFF2CD9E))
val GradientOrangeRevert = Gradient(Color(0xFFF2CD9E), Orange)
val GradientIvy = Gradient(Ivy, Color(0xFFAA99FF))


fun Color.asBrush(): Brush {
    return Brush.horizontalGradient(colors = listOf(this, this))
}

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

@Composable
fun pureBlur() = UI.colors.pure.copy(alpha = 0.95f)

@Composable
fun mediumBlur() = UI.colors.medium.copy(alpha = 0.95f)

@Composable
fun gradientExpenses() = Gradient(UI.colors.pureInverse, UI.colors.gray)

data class IvyColors(
    val pure: Color,
    val pureInverse: Color,

    val gray: Color,
    val medium: Color,
    val mediumInverse: Color,

    val ivy: Color,
    val ivy1: Color,

    val green: Color,
    val green1: Color,

    val orange: Color,
    val orange1: Color,

    val red: Color,
    val red1: Color,

    val isLight: Boolean
)

data class Gradient(
    val startColor: Color,
    val endColor: Color
) {
    companion object {
        fun from(gradient: com.ivy.design.l0_system.Gradient) =
            Gradient(gradient.startColor, gradient.endColor)

        fun from(startColor: Int, endColor: Int?) = Gradient(
            startColor = startColor.toComposeColor(),
            endColor = (endColor ?: startColor).toComposeColor()
        )

        fun solid(color: Color) = Gradient(color, color)

        @Composable
        fun black() = Gradient(UI.colors.gray, UI.colors.pureInverse)
    }

    fun asHorizontalBrush() = Brush.horizontalGradient(colors = listOf(startColor, endColor))

    fun asVerticalBrush() = Brush.verticalGradient(colors = listOf(startColor, endColor))
}

fun findContrastTextColor(backgroundColor: Color): Color {
    return if (isDarkColor(backgroundColor.toArgb())) White else Black
}

fun isDarkColor(color: Color): Boolean {
    return isDarkColor(color.toArgb())
}

fun isDarkColor(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) <= 0.5
}

fun Color.dynamicContrast(): Color {
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

fun Color.lighten(): Color {
    return this.hsv(
        s = 0.3f,
        v = 1f
    )
}

fun Color.darken(): Color {
    return this.hsv(
        s = 0.6f,
        v = 0.5f
    )
}

fun Color.toHSVSpec(): HSVSpec {
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

fun Int.toComposeColor() = Color(this)