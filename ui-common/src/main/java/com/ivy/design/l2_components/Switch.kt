package com.ivy.design.l2_components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.utils.ComponentPreviewBase
import com.ivy.design.utils.springBounce

@Composable
fun Switch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    enabledColor: Color = UI.colors.green,
    disabledColor: Color = UI.colors.gray,
    animationColor: AnimationSpec<Color> = springBounce(),
    animationMove: AnimationSpec<Float> = springBounce(),
    onEnabledChange: (checked: Boolean) -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (enabled) enabledColor else disabledColor,
        animationSpec = animationColor
    )

    Row(
        modifier = modifier
            .width(40.dp)
            .clip(UI.shapes.rFull)
            .border(2.dp, color, UI.shapes.rFull)
            .clickable {
                onEnabledChange(!enabled)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val weightStart by animateFloatAsState(
            targetValue = if (enabled) 1f else 0f,
            animationSpec = animationMove
        )

        SpacerHor(width = 4.dp)

        if (weightStart > 0) {
            SpacerWeight(weight = weightStart)
        }

        //Circle
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )

        val weightEnd = 1f - weightStart
        if (weightEnd > 0) {
            SpacerWeight(weight = weightEnd)
        }

        SpacerHor(width = 4.dp)
    }
}

@Preview
@Composable
private fun PreviewIvySwitch() {
    ComponentPreviewBase {
        var enabled by remember {
            mutableStateOf(false)
        }

        Switch(enabled = enabled) {
            enabled = it
        }
    }
}