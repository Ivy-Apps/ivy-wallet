package com.ivy.wallet.ui.theme.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.utils.springBounce


@Composable
fun IvySwitch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onEnabledChange: (checked: Boolean) -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (enabled) Green else Gray,
        animationSpec = springBounce()
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
            animationSpec = springBounce()
        )

        Spacer(Modifier.width(4.dp))

        if (weightStart > 0) {
            Spacer(Modifier.weight(weightStart))
        }

        //Circle
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )

        val weightEnd = 1f - weightStart
        if (weightEnd > 0) {
            Spacer(Modifier.weight(weightEnd))
        }

        Spacer(Modifier.width(4.dp))
    }
}

@Preview
@Composable
private fun PreviewIvySwitch() {
    com.ivy.core.ui.temp.ComponentPreview {
        var enabled by remember {
            mutableStateOf(false)
        }

        IvySwitch(enabled = enabled) {
            enabled = it
        }
    }
}