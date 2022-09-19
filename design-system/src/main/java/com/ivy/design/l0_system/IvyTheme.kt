package com.ivy.design.l0_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.ivy.design.Theme
import com.ivy.design.api.IvyDesign

val LocalIvyColors = compositionLocalOf<IvyColors> { error("No IvyColors") }
val LocalIvyTypography = compositionLocalOf<IvyTypography> { error("No IvyTypography") }
val LocalIvyShapes = compositionLocalOf<IvyShapes> { error("No IvyShapes") }

object UI {
    val colors: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColors.current

    val typo: IvyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyTypography.current

    val shapes: IvyShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyShapes.current
}

@Composable
fun IvyTheme(
    theme: Theme,
    design: IvyDesign,
    content: @Composable () -> Unit
) {
    val colors = design.colors(theme, isSystemInDarkTheme())
    val typography = design.typography()
    val shapes = design.shapes()

    CompositionLocalProvider(
        LocalIvyColors provides colors,
        LocalIvyTypography provides typography,
        LocalIvyShapes provides shapes
    ) {
        MaterialTheme(
            colors = toMaterial(colors),
            typography = toMaterial(typography),
            shapes = toMaterial(shapes),
            content = content
        )
    }
}

fun toMaterial(colors: IvyColors): Colors {
    return Colors(
        primary = colors.primary,
        primaryVariant = colors.primary1,
        secondary = colors.primary,
        secondaryVariant = colors.primary1,
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
        large = shapes.r1,
        medium = shapes.r2,
        small = shapes.r3
    )
}
