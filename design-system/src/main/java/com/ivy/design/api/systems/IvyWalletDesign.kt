package com.ivy.design.api.systems

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.R
import com.ivy.design.Theme
import com.ivy.design.api.IvyDesign
import com.ivy.design.l0_system.*


fun ivyWalletDesign(theme: Theme, isSystemInDarkTheme: Boolean): IvyDesign = IvyDesign(
    typography = typography(),
    colors = colors(theme = theme, isSystemInDarkTheme = isSystemInDarkTheme),
    shapes = shapes()
)

private const val OPEN_SANS_BASELINE_SHIFT = 0.075f
private const val RALEWAY_BASELINE_SHIFT = 0.2f

private fun typography(): IvyTypography {
    val openSans = FontFamily(
        Font(R.font.opensans_regular, FontWeight.Normal),
        Font(R.font.opensans_regular, FontWeight.Medium),
        Font(R.font.opensans_bold, FontWeight.Black),
        Font(R.font.opensans_semibold, FontWeight.SemiBold),
        Font(R.font.opensans_bold, FontWeight.Bold),
        Font(R.font.opensans_extrabold, FontWeight.ExtraBold),
    )

    val raleWay = FontFamily(
        Font(R.font.raleway_regular, FontWeight.Normal),
        Font(R.font.raleway_medium, FontWeight.Medium),
        Font(R.font.raleway_black, FontWeight.Black),
        Font(R.font.raleway_light, FontWeight.Light),
        Font(R.font.raleway_semibold, FontWeight.SemiBold),
        Font(R.font.raleway_bold, FontWeight.Bold),
        Font(R.font.raleway_extrabold, FontWeight.ExtraBold),
    )

    val h1 = 40.sp
    val h2 = 32.sp
    val b1 = 20.sp
    val b2 = 16.sp
    val c = 12.sp

    return IvyTypography(
        h1 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Black,
            fontSize = h1,
            baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        h2 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = h2,
            baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        b1 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Bold,
            fontSize = b1,
            baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        b2 = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.Medium,
            fontSize = b2,
            baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        c = TextStyle(
            fontFamily = raleWay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = c,
            baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),

        nH1 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = h1,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        nH2 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = h2,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        nB1 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = b1,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        nB2 = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Normal,
            fontSize = b2,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        nC = TextStyle(
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize = c,
            baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        )
    )
}

private fun colors(theme: Theme, isSystemInDarkTheme: Boolean): IvyColors = when (theme) {
    Theme.Light -> IvyColors(
        pure = White,
        pureInverse = Black,
        gray = Gray,
        medium = MediumWhite,
        mediumInverse = MediumBlack,

        primary = Purple,
        primary1 = IvyDark,

        green = Green,
        green1 = GreenLight,

        orange = Orange,
        orange1 = OrangeLight,

        red = Red,
        red1 = RedLight,
        red1Inverse = RedDark,

        isLight = true
    )
    Theme.Dark -> IvyColors(
        pure = Black,
        pureInverse = White,
        gray = Gray,
        medium = MediumBlack,
        mediumInverse = MediumWhite,

        primary = Purple,
        primary1 = IvyLight,

        green = Green,
        green1 = GreenDark,

        orange = Orange,
        orange1 = OrangeDark,

        red = Red,
        red1 = RedDark,
        red1Inverse = RedLight,

        isLight = false
    )
    Theme.Auto -> if (isSystemInDarkTheme)
        colors(Theme.Dark, true) else
        colors(Theme.Light, false)
}


private fun shapes(): IvyShapes = IvyShapes(
    r1 = RoundedCornerShape(32.dp),
    r1Top = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    r1Bot = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),

    r2 = RoundedCornerShape(24.dp),
    r2Top = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    r2Bot = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),

    r3 = RoundedCornerShape(20.dp),
    r3Top = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    r3Bot = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),

    r4 = RoundedCornerShape(16.dp),
    r4Top = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    r4Bot = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
)