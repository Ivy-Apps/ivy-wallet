package com.ivy.design.l0_system.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.ivy.design.l0_system.UI

// region Monochrome
val White = Color(0xFFFAFAFA)
val Black = Color(0xFF111114)
val MediumBlack = Color(0xFF2B2C2D)
val Gray = Color(0xFF939199)
val MediumWhite = Color(0xFFEFEEF0)
val Transparent = Color(0x00000000)
// endregion

// region Primary
val Purple = Color(0xFF6B4DFF)
val Purple1 = Color(0xFFC34CFF)
val Purple2 = Color(0xFFFF4CFF)

val Blue = Color(0xFF4CC3FF)
val Blue2 = Color(0xFF45E6E6)
val Blue3 = Color(0xFF457BE6)

val Green = Color(0xFF14CC9E)
val Green2 = Color(0xFF45E67B)
val Green3 = Color(0xFF96E645)
val Green4 = Color(0xFFC7E62E)

val Yellow = Color(0xFFF2E230)

val Orange = Color(0xFFF29F30)
val Orange2 = Color(0xFFE67B45)
val Orange3 = Color(0xFFFFC34C)

val Red = Color(0xFFFF4060)
val Red2 = Color(0xFFE62E2E)
val Red3 = Color(0xFFFF4CA6)
// endregion

// region Light variants of primary
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

val YellowLight = Color(0xFFFFF9B2)
val YellowP1Light = Color(0xFFFFF266)

val OrangeLight = Color(0xFFFFDEB3)
val Orange2Light = Color(0xFFFFCCB3)
val Orange3Light = Color(0xFFFFDC99)

val RedLight = Color(0xFFFFCCD5)
val Red2Light = Color(0xFFFFB3B3)
val Red3Light = Color(0xFFFFCCE6)
// endregion

// region Dark variations of primary
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

val YellowDark = Color(0xFF806A00)
val YellowP1Dark = Color(0xFFBFA730)


val OrangeDark = Color(0xFF734B17)
val Orange2Dark = Color(0xFF66371F)
val Orange3Dark = Color(0xFF806226)

val RedDark = Color(0xFF801919)
val Red2Dark = Color(0xFF802030)
val Red3Dark = Color(0xFF802653)
// endregion

// region Gradients
val GradientRed = Gradient(Red, Color(0xFFFF99AB))
val GradientGreen = Gradient(Green, Color(0xFF49F2C8))
val GradientOrange = Gradient(Orange, OrangeLight)
val GradientOrangeDark = Gradient(OrangeDark, Color(0xFFF2CD9E))
val GradientOrangeRevert = Gradient(Color(0xFFF2CD9E), Orange)
val GradientPurple = Gradient(Purple, Color(0xFFAA99FF))
val SunsetNight = Gradient(Red, Orange)

@Immutable
data class Gradient(
    val start: Color,
    val end: Color
) {
    companion object {
        fun from(startColor: Int, endColor: Int? = null) = Gradient(
            start = startColor.toComposeColor(),
            end = (endColor ?: startColor).toComposeColor()
        )

        fun solid(color: Color) = Gradient(color, color)

        fun neutral(lightTheme: Boolean) = Gradient(
            start = Gray,
            end = if (lightTheme) Black else White
        )

        @Composable
        fun black() = Gradient(UI.colors.neutral, UI.colorsInverted.pure)
    }

    fun asHorizontalBrush() = Brush.horizontalGradient(colors = listOf(start, end))

    fun asVerticalBrush() = Brush.verticalGradient(colors = listOf(start, end))
}
// endregion

@Composable
fun pureBlur() = UI.colors.pure.copy(alpha = 0.95f)

@Composable
fun mediumBlur() = UI.colors.medium.copy(alpha = 0.95f)