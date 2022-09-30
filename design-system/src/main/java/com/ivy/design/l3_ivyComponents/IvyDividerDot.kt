package com.ivy.design.l3_ivyComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.util.ComponentPreview

@Composable
fun IvyDividerDot(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .size(4.dp)
            .background(UI.colorsInverted.medium, CircleShape)
    )
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        IvyDividerDot()
    }
}