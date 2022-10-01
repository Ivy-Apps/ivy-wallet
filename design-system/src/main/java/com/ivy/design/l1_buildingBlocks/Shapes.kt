package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.util.ComponentPreview

@Composable
fun Shape(
    size: Dp,
    shape: Shape,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .size(size)
            .background(color = color, shape = shape)
    )
}

@Composable
fun ShapeOutlined(
    size: Dp,
    shape: Shape,
    borderColor: Color,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
) {
    Spacer(
        modifier = modifier
            .size(size)
            .border(
                color = borderColor,
                width = borderWidth,
                shape = shape
            )
    )
}

@Composable
fun Shape(modifier: Modifier = Modifier) {
    Spacer(modifier)
}


// region Previews
@Preview
@Composable
private fun Preview_Circle() {
    ComponentPreview {
        Shape(
            size = 32.dp,
            shape = UI.shapes.circle,
            color = UI.colors.primary
        )
    }
}

@Preview
@Composable
private fun PreviewOutlined() {
    ComponentPreview {
        ShapeOutlined(
            size = 64.dp,
            shape = UI.shapes.rounded,
            borderWidth = 2.dp,
            borderColor = UI.colors.neutral
        )
    }
}
// endregion