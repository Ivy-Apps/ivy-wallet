package com.ivy.design.system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ivy.design.system.colors.IvyColors

@Composable
fun IvyM3Theme(
    dark: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) ivyDarkColorScheme() else ivyLightColorScheme(),
        content = content,
    )
}

private fun ivyLightColorScheme(): ColorScheme = ColorScheme(
    primary = IvyColors.Purple.primary,
    onPrimary = IvyColors.White,
    primaryContainer = IvyColors.Purple.light,
    onPrimaryContainer = IvyColors.White,
    inversePrimary = IvyColors.Purple.dark,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.White,
    secondaryContainer = IvyColors.Green.light,
    onSecondaryContainer = IvyColors.White,
    tertiary = IvyColors.Blue.primary,
    onTertiary = IvyColors.White,
    tertiaryContainer = IvyColors.Blue.light,
    onTertiaryContainer = IvyColors.White,
    background = IvyColors.White,
    onBackground = IvyColors.Black,
    surface = IvyColors.White,
    onSurface = IvyColors.Black,
    surfaceVariant = IvyColors.White,
    onSurfaceVariant = IvyColors.Black,
    surfaceTint = IvyColors.Black,
    inverseSurface = IvyColors.DarkGray,
    inverseOnSurface = IvyColors.White,
    error = IvyColors.Red.primary,
    onError = IvyColors.White,
    errorContainer = IvyColors.Red.light,
    onErrorContainer = IvyColors.White,
    outline = IvyColors.Gray,
    outlineVariant = IvyColors.DarkGray,
    scrim = IvyColors.Black.copy(alpha = 0.8f)
)

private fun ivyDarkColorScheme(): ColorScheme = ColorScheme(
    primary = IvyColors.Purple.primary,
    onPrimary = IvyColors.Black,
    primaryContainer = IvyColors.Purple.dark,
    onPrimaryContainer = IvyColors.Black,
    inversePrimary = IvyColors.Purple.light,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.Black,
    secondaryContainer = IvyColors.Green.dark,
    onSecondaryContainer = IvyColors.Black,
    tertiary = IvyColors.Blue.primary,
    onTertiary = IvyColors.Black,
    tertiaryContainer = IvyColors.Blue.dark,
    onTertiaryContainer = IvyColors.Black,
    background = IvyColors.Black,
    onBackground = IvyColors.White,
    surface = IvyColors.ExtraDarkGray,
    onSurface = IvyColors.White,
    surfaceVariant = IvyColors.DarkGray,
    onSurfaceVariant = IvyColors.White,
    surfaceTint = IvyColors.Gray,
    inverseSurface = IvyColors.LightGray,
    inverseOnSurface = IvyColors.Black,
    error = IvyColors.Red.primary,
    onError = IvyColors.Black,
    errorContainer = IvyColors.Red.dark,
    onErrorContainer = IvyColors.Black,
    outline = IvyColors.Gray,
    outlineVariant = IvyColors.DarkGray,
    scrim = IvyColors.Black.copy(alpha = 0.8f)
)