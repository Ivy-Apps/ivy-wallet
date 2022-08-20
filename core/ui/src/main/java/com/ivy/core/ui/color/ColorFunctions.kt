package com.ivy.core.ui.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.ivy.design.l0_system.*

/**
 * @return Black or White so it maximizes contrast
 */
@Suppress("DEPRECATION")
@Composable
fun Color.contrast(): Color = remember(this) {
    // Optimize black and white colors
    when (this) {
        Black -> White
        White -> Black
        else -> findContrastTextColor(this)
    }
}

/**
 * @return Similar color but such that it'll have contrast.
 */
@Composable
fun Color.dynamicContrast(): Color = remember(this) {
    val color = this.toHSVSpec()
    return when {
        color.s >= 0.5f && color.v >= 0.4f -> {
            //Primary
            if (isDarkColor(this)) {
                lighten()
            } else {
                darken()
            }
        }
        color.s <= 0.5f && color.v >= 0.8f -> {
            //Light
            darken()
        }
        color.s >= 0.1f && color.v <= 0.6f -> {
            //Dark
            lighten()
        }
        else -> {
            if (isDarkColor(this)) {
                lighten()
            } else {
                darken()
            }
        }
    }
}