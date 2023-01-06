package com.ivy.design.l2_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.util.ComponentPreview

@Composable
fun SwitchRow(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.rounded)
            .defaultMinSize(minHeight = 48.dp)
            .clickable {
                onValueChange(!enabled)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        B2(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            text = text
        )
        Switch(enabled = enabled, onEnabledChange = onValueChange)
    }
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        SwitchRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = true,
            text = "Switch Row",
            onValueChange = {}
        )
    }
}