package com.ivy.core.ui.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.ivy.design.l0_system.Black
import com.ivy.design.l0_system.White
import com.ivy.design.l0_system.findContrastTextColor

@Composable
fun Color.contrastColor(): Color = remember(this) {
    // Optimize black and white colors
    when (this) {
        Black -> White
        White -> Black
        else -> findContrastTextColor(this)
    }
}