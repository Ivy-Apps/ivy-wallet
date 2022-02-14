package com.ivy.wallet.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.base.setStatusBarDarkTextCompat

val LocalIvyColors = compositionLocalOf<IvyColors> { error("No IvyColors") }


object IvyTheme {
    val colors: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColors.current
}

@Composable
fun IvyTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val ivyColors = adaptIvyColors(theme)

    setStatusBarDarkTextCompat(darkText = ivyColors.isLight)

    CompositionLocalProvider(
        LocalIvyColors provides ivyColors
    ) {
        MaterialTheme(
            colors = adaptTheme(ivyColors),
            typography = adaptTypography(Typo),
            shapes = adaptShapes(Shapes),
            content = content
        )
    }
}

fun adaptTheme(colors: IvyColors): Colors {
        return Colors(
                primary = colors.ivy,
                primaryVariant = colors.ivy1,
                secondary = colors.ivy,
                secondaryVariant = colors.ivy1,
                background = colors.pure,
                surface = colors.pure,
                onSurface = colors.pureInverse,
                error = colors.red,
                onPrimary = White,
                onSecondary = White,
                onBackground = colors.pureInverse,
                onError = White,
                isLight = colors.isLight
        )
}

fun adaptShapes(shapes: IvyShapes): Shapes {
        return Shapes(
            large = shapes.rounded32,
            medium = shapes.rounded24,
            small = shapes.rounded16
        )
}

fun adaptTypography(typography: IvyTypography) : Typography {
        return Typography(
                h1 = typography.h1,
                h2 = typography.h2,
                body1 = typography.body1,
                body2 = typography.body2,
                caption = typography.caption
        )
}

fun adaptIvyColors(theme: Theme): IvyColors {
    return when (theme) {
        Theme.LIGHT -> IvyColors(
                pure = White,
                pureInverse = Black,
                gray = Gray,
                medium = MediumWhite,
                mediumInverse = MediumBlack,

                ivy = Ivy,
                ivy1 = IvyDark,

                green = Green,
                green1 = GreenLight,

                orange = Orange,
                orange1 = OrangeLight,

                red = Red,
                red1 = RedLight,

                isLight = true
        )
        Theme.DARK -> IvyColors(
                pure = Black,
                pureInverse = White,
                gray = Gray,
                medium = MediumBlack,
                mediumInverse = MediumWhite,

                ivy = Ivy,
                ivy1 = IvyLight,

                green = Green,
                green1 = GreenDark,

                orange = Orange,
                orange1 = OrangeDark,

                red = Red,
                red1 = RedDark,

                isLight = false
        )
    }
}