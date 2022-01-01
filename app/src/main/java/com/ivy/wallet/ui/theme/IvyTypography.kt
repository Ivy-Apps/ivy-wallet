package com.ivy.wallet.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ivy.wallet.R

const val OPEN_SANS_BASELINE_SHIFT = 0.075f
const val RALEWAY_BASELINE_SHIFT = 0.2f

val OpenSans = FontFamily(
        Font(R.font.opensans_regular, FontWeight.Normal),
        Font(R.font.opensans_regular, FontWeight.Medium),
        Font(R.font.opensans_bold, FontWeight.Black),
        Font(R.font.opensans_semibold, FontWeight.SemiBold),
        Font(R.font.opensans_bold, FontWeight.Bold),
        Font(R.font.opensans_extrabold, FontWeight.ExtraBold),
)

val RaleWay = FontFamily(
        Font(R.font.raleway_regular, FontWeight.Normal),
        Font(R.font.raleway_medium, FontWeight.Medium),
        Font(R.font.raleway_black, FontWeight.Black),
        Font(R.font.raleway_light, FontWeight.Light),
        Font(R.font.raleway_semibold, FontWeight.SemiBold),
        Font(R.font.raleway_bold, FontWeight.Bold),
        Font(R.font.raleway_extrabold, FontWeight.ExtraBold),
)

val Typo = IvyTypography(
        h1 = TextStyle(
                fontFamily = RaleWay,
                fontWeight = FontWeight.Black,
                fontSize = 42.sp,
                baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        h2 = TextStyle(
                fontFamily = RaleWay,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        body1 = TextStyle(
                fontFamily = RaleWay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        body2 = TextStyle(
                fontFamily = RaleWay,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),
        caption = TextStyle(
                fontFamily = RaleWay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                baselineShift = BaselineShift(RALEWAY_BASELINE_SHIFT),
        ),

        numberH1 = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        numberH2 = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        numberBody1 = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        numberBody2 = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
        numberCaption = TextStyle(
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                baselineShift = BaselineShift(OPEN_SANS_BASELINE_SHIFT),
        ),
)

data class IvyTypography(
        val h1: TextStyle,
        val h2: TextStyle,
        val body1: TextStyle,
        val body2: TextStyle,
        val caption: TextStyle,

        val numberH1: TextStyle,
        val numberH2: TextStyle,
        val numberBody1: TextStyle,
        val numberBody2: TextStyle,
        val numberCaption: TextStyle
)

fun TextStyle.colorAs(color: Color) = this.copy(color = color)

@Composable
fun TextStyle.style(
        color: Color = IvyTheme.colors.pureInverse,
        fontWeight: FontWeight = FontWeight.Bold,
        textAlign: TextAlign = TextAlign.Start,
) = this.copy(
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
)