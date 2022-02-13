package com.ivy.design.api.systems

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.R
import com.ivy.design.api.IvyDesign
import com.ivy.design.l0_system.*

abstract class IvyWalletDesign : IvyDesign {

    override fun typography(): IvyTypography {
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

        return object : IvyTypography {
            override val h1 = TextStyle(
                fontFamily = raleWay,
                fontWeight = FontWeight.Black,
                fontSize = h1
            )
            override val h2 = TextStyle(
                fontFamily = raleWay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = h2
            )
            override val b1 = TextStyle(
                fontFamily = raleWay,
                fontWeight = FontWeight.Bold,
                fontSize = b1
            )
            override val b2 = TextStyle(
                fontFamily = raleWay,
                fontWeight = FontWeight.Medium,
                fontSize = b2
            )
            override val c = TextStyle(
                fontFamily = raleWay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = c
            )

            override val nH1 = TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = h1
            )
            override val nH2 = TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = h2
            )
            override val nB1 = TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = b1
            )
            override val nB2 = TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Normal,
                fontSize = b2
            )
            override val nC = TextStyle(
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = c
            )
        }
    }

    override fun colors(theme: Theme): IvyColors {
        return when (theme) {
            Theme.LIGHT -> object : IvyColors {
                override val pure = White
                override val pureInverse = Black
                override val gray = Gray
                override val medium = MediumWhite
                override val mediumInverse = MediumBlack

                override val primary = Purple
                override val primary1 = IvyDark

                override val green = Green
                override val green1 = GreenLight

                override val orange = Orange
                override val orange1 = OrangeLight

                override val red = Red
                override val red1 = RedLight

                override val isLight = true
            }
            Theme.DARK -> object : IvyColors {
                override val pure = Black
                override val pureInverse = White
                override val gray = Gray
                override val medium = MediumBlack
                override val mediumInverse = MediumWhite

                override val primary = Purple
                override val primary1 = IvyLight

                override val green = Green
                override val green1 = GreenDark

                override val orange = Orange
                override val orange1 = OrangeDark

                override val red = Red
                override val red1 = RedDark

                override val isLight = false
            }
        }
    }

    override fun shapes(): IvyShapes {
        return object : IvyShapes() {
            override val r1 = RoundedCornerShape(32.dp)
            override val r1top = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            override val r1bot = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)

            override val r2 = RoundedCornerShape(24.dp)
            override val r2top = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            override val r2bot = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)

            override val r3 = RoundedCornerShape(16.dp)
            override val r3top = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            override val r3bot = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)

            override val r4 = RoundedCornerShape(8.dp)
            override val r4top = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            override val r4bot = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
        }
    }
}