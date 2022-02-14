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
import com.ivy.design.utils.IvyComponentPreview

@Composable
fun Shape(
    modifier: Modifier = Modifier,
    size: Dp,
    shape: Shape,
    color: Color,
) {
    Spacer(
        modifier = Modifier
            .size(size)
            .background(
                color = color,
                shape = shape
            )
    )
}

@Composable
fun ShapeOutlined(
    modifier: Modifier = Modifier,
    size: Dp,
    shape: Shape,
    borderColor: Color,
    borderWidth: Dp = 1.dp,
) {
    Spacer(
        modifier = Modifier
            .size(size)
            .border(
                color = borderColor,
                width = borderWidth,
                shape = shape
            )
    )
}

@Preview
@Composable
private fun Preview_Circle() {
    IvyComponentPreview {
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
    IvyComponentPreview {
        ShapeOutlined(
            size = 64.dp,
            shape = UI.shapes.r3,
            borderWidth = 2.dp,
            borderColor = UI.colors.gray
        )
    }
}