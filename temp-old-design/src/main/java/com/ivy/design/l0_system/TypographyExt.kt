package com.ivy.design.l0_system

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Deprecated("Old design system. Use `:ivy-design` and Material3")
fun TextStyle.colorAs(color: Color) = this.copy(color = color)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun TextStyle.style(
    color: Color = UI.colors.pureInverse,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Start
) = this.copy(
    color = color,
    fontWeight = fontWeight,
    textAlign = textAlign
)
