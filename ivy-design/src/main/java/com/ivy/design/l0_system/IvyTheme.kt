package com.ivy.design.l0_system

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
        val colorScheme = adaptColors(colors)

        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    colors.isLight
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = adaptTypography(typography),
            shapes = adaptShapes(shapes),
            content = content
        )
    }
}

fun adaptColors(colors: IvyColors): ColorScheme {
    val colorScheme = if (colors.isLight) {
        lightColorScheme()
    } else {
        darkColorScheme()
    }
    return colorScheme.copy(
        primary = colors.primary,
        onPrimary = White,
        secondary = colors.primary,
        onSecondary = White,
        background = colors.pure,
        onBackground = colors.pureInverse,
        surface = colors.pure,
        onSurface = colors.pureInverse,
        error = colors.red,
        onError = White,
        scrim = colors.gray,
    )
}

fun adaptTypography(typography: IvyTypography): Typography {
    return Typography(
        headlineLarge = typography.h1,
        headlineMedium = typography.h2,
        bodyLarge = typography.b1,
        bodyMedium = typography.b2,
        bodySmall = typography.c
    )
}

fun adaptShapes(shapes: IvyShapes): Shapes {
    return Shapes(
        large = shapes.r1,
        medium = shapes.r2,
        small = shapes.r3
    )
}
