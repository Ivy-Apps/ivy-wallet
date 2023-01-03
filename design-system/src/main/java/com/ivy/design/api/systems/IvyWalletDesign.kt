package com.ivy.design.api.systems

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.data.Theme
import com.ivy.design.R
import com.ivy.design.api.IvyDesign
import com.ivy.design.l0_system.IvyShapes
import com.ivy.design.l0_system.IvyTypography
import com.ivy.design.l0_system.color.*


fun ivyWalletDesign(theme: Theme, isSystemInDarkTheme: Boolean): IvyDesign = IvyDesign(
    typography = typography(),
    typographySecondary = typographySecondary(),
    colors = colors(theme = theme, isSystemInDarkTheme = isSystemInDarkTheme),
    colorsInverted = colors(
        theme = when (theme) {
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
            Theme.Auto -> Theme.Auto
        },
        isSystemInDarkTheme = !isSystemInDarkTheme
    ),
    shapes = shapes()
)

// region Typography
private const val OPEN_SANS_BASELINE_SHIFT = 0.075f
private const val RALE_WAY_BASELINE_SHIFT = 0.2f

private val h1 = 40.sp
private val h2 = 32.sp
private val b1 = 20.sp
private val b2 = 16.sp
private val c = 12.sp


private fun typography(): IvyTypography {
    val raleWay = FontFamily(
        Font(R.font.raleway_regular, FontWeight.Normal),
        Font(R.font.raleway_medium, FontWeight.Medium),
        Font(R.font.raleway_black, FontWeight.Black),
        Font(R.font.raleway_light, FontWeight.Light),
        Font(R.font.raleway_semibold, FontWeight.SemiBold),
        Font(R.font.raleway_bold, FontWeight.Bold),
        Font(R.font.raleway_extrabold, FontWeight.ExtraBold),
    )

    return IvyTypography(
        h1 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Black,
            fontSize = h1,
            baselineShift = BaselineShift(RALE_WAY_BASELINE_SHIFT),
        ),
        h2 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = h2,
            baselineShift = BaselineShift(RALE_WAY_BASELINE_SHIFT),
        ),
        b1 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Bold,
            fontSize = b1,
            baselineShift = BaselineShift(RALE_WAY_BASELINE_SHIFT),
        ),
        b2 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Medium,
            fontSize = b2,
            baselineShift = BaselineShift(RALE_WAY_BASELINE_SHIFT),
        ),
        c = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = c,
            baselineShift = BaselineShift(RALE_WAY_BASELINE_SHIFT),
        ),
    )
}

private fun typographySecondary(): IvyTypography {
    val openSans = FontFamily(
        Font(R.font.opensans_regular, FontWeight.Normal),
        Font(R.font.opensans_regular, FontWeight.Medium),
        Font(R.font.opensans_bold, FontWeight.Black),
        Font(R.font.opensans_semibold, FontWeight.SemiBold),
        Font(R.font.opensans_bold, FontWeight.Bold),
        Font(R.font.opensans_extrabold, FontWeight.ExtraBold),
    )

    return IvyTypography(
        h1 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = h1,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        h2 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = h2,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        b1 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = b1,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        b2 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Normal,
            fontSize = b2,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        c = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = c,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        )
    )
}
// endregion

// region Colors
private fun colors(theme: Theme, isSystemInDarkTheme: Boolean): IvyColors = when (theme) {
    Theme.Light -> IvyColors(
        pure = White,
        neutral = Gray,
        medium = MediumWhite,

        primary = Purple,
        primaryP1 = Purple2Light,
        primaryP2 = Purple1Light,

        red = Red,
        redP1 = Red2Light,
        redP2 = RedLight,

        orange = Orange,
        orangeP1 = Orange2Light,
        orangeP2 = OrangeLight,

        yellow = Yellow,
        yellowP1 = YellowP1Light,
        yellowP2 = YellowLight,

        green = Green,
        greenP1 = Green2Light,
        greenP2 = GreenLight,

        blue = Blue,
        blueP1 = Blue2Light,
        blueP2 = BlueLight,

        purple = Purple,
        purpleP1 = Purple2Light,
        purpleP2 = Purple1Light,

        isLight = true
    )
    Theme.Dark -> IvyColors(
        pure = Black,
        neutral = Gray,
        medium = MediumBlack,

        primary = Purple,
        primaryP1 = Purple2Dark,
        primaryP2 = Purple1Dark,

        red = Red,
        redP1 = Red2Dark,
        redP2 = RedDark,

        orange = Orange,
        orangeP1 = Orange2Dark,
        orangeP2 = OrangeDark,

        yellow = Yellow,
        yellowP1 = YellowP1Dark,
        yellowP2 = YellowDark,

        green = Green,
        greenP1 = Green2Dark,
        greenP2 = GreenDark,

        blue = Blue,
        blueP1 = Blue2Dark,
        blueP2 = BlueDark,

        purple = Purple,
        purpleP1 = Purple2Dark,
        purpleP2 = Purple1Dark,

        isLight = false
    )
    Theme.Auto -> if (isSystemInDarkTheme)
        colors(Theme.Dark, true) else
        colors(Theme.Light, false)
}
// endregion

private fun shapes(): IvyShapes {
    val rSquared = 8.dp
    val rRounded = 24.dp

    return IvyShapes(
        squared = RoundedCornerShape(rSquared),
        squaredTop = RoundedCornerShape(topStart = rSquared, topEnd = rSquared),
        squaredBottom = RoundedCornerShape(bottomStart = rSquared, bottomEnd = rSquared),
        rounded = RoundedCornerShape(rRounded),
        roundedTop = RoundedCornerShape(topStart = rRounded, topEnd = rRounded),
        roundedBottom = RoundedCornerShape(bottomStart = rRounded, bottomEnd = rRounded),
        fullyRounded = RoundedCornerShape(percent = 50),
    )
}