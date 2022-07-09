package com.ivy.old

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI



@Composable
fun OnboardingProgressSlider(
    modifier: Modifier = Modifier,
    selectedStep: Int,
    stepsCount: Int,
    selectedColor: Color
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until stepsCount) {
            val selected = selectedStep == i
            Line(
                width = if (selected) 48.dp else 24.dp,
                color = if (selected) selectedColor else UI.colors.medium
            )

            if (i < stepsCount - 1) {
                Spacer(Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun Line(
    width: Dp,
    color: Color
) {
    Spacer(
        modifier = Modifier
            .size(width = width, height = 4.dp)
            .background(color, UI.shapes.rFull)
    )
}