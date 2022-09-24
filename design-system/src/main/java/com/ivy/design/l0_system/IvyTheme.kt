package com.ivy.design.l0_system

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import com.ivy.design.api.IvyDesign
import com.ivy.design.l0_system.color.IvyColors
import com.ivy.design.l0_system.color.contrastColor

val LocalIvyColors = compositionLocalOf<IvyColors> { error("No colors") }
val LocalIvyColorsInverted = compositionLocalOf<IvyColors> { error("No inverted colors") }
val LocalIvyTypo = compositionLocalOf<IvyTypography> { error("No typography") }
val LocalIvyTypoSecondary = compositionLocalOf<IvyTypography> { error("No secondary typography") }
val LocalIvyShapes = compositionLocalOf<IvyShapes> { error("No shapes") }

object UI {
    val colors: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColors.current

    val colorsInverted: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColorsInverted.current

    val typo: IvyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyTypo.current

    val typoSecond: IvyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyTypoSecondary.current


    val shapes: IvyShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyShapes.current
}

@Composable
fun IvyTheme(
    design: IvyDesign,
    content: @Composable () -> Unit
) {
    val colors = design.colors
    val colorsInverted = design.colorsInverted
    val typography = design.typography
    val shapes = design.shapes

    CompositionLocalProvider(
        LocalIvyColors provides colors,
        LocalIvyColorsInverted provides colorsInverted,
        LocalIvyTypo provides typography,
        LocalIvyTypoSecondary provides design.typographySecondary,
        LocalIvyShapes provides shapes
    ) {
        val materialColors = remember(colors, colorsInverted) { toMaterial(colors, colorsInverted) }
        val materialTypography = remember(typography) { toMaterial(typography) }
        val materialShapes = remember(shapes) { toMaterial(shapes) }

        MaterialTheme(
            colors = materialColors,
            typography = materialTypography,
            shapes = materialShapes,
            content = content
        )
    }
}

fun toMaterial(colors: IvyColors, colorsInverted: IvyColors): Colors {
    return Colors(
        primary = colors.primary,
        primaryVariant = colors.primaryP1,
        secondary = colors.primary,
        secondaryVariant = colors.primaryP1,
        background = colors.pure,
        surface = colors.pure,
        onSurface = colorsInverted.pure,
        error = colors.red,
        onPrimary = contrastColor(colors.primary),
        onSecondary = contrastColor(colors.primary),
        onBackground = colorsInverted.pure,
        onError = contrastColor(colors.red),
        isLight = colors.isLight
    )
}

fun toMaterial(typography: IvyTypography): Typography {
    return Typography(
        h1 = typography.h1,
        h2 = typography.h2,
        body1 = typography.b1,
        body2 = typography.b2,
        caption = typography.c
    )
}

fun toMaterial(shapes: IvyShapes): Shapes {
    return Shapes(
        large = shapes.fullyRounded,
        medium = shapes.rounded,
        small = shapes.squared
    )
}
