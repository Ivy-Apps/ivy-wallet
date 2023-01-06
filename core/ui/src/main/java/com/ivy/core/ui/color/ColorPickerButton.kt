package com.ivy.core.ui.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l0_system.color.toHex
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.ComponentPreview

@Composable
fun ColorPickerButton(
    colorPickerModal: IvyModal,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 24.dp,
    paddingVertical: Dp = 24.dp,
) {
    ColorButton(
        color = selectedColor,
        modifier = modifier,
        paddingHorizontal = paddingHorizontal,
        paddingVertical = paddingVertical,
    ) {
        colorPickerModal.show()
    }
}

@Composable
fun ColorButton(
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = UI.shapes.rounded,
    paddingHorizontal: Dp = 24.dp,
    paddingVertical: Dp = 24.dp,
    onClick: () -> Unit,
) {
    val colorHex = remember(color) { color.toHex() }
    val dynamicContrast = rememberDynamicContrast(color)
    B1Second(
        modifier = modifier
            .clip(shape)
            .background(color, shape)
            .border(width = 2.dp, color = dynamicContrast, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
        text = "#$colorHex",
        fontWeight = FontWeight.ExtraBold,
        color = dynamicContrast,
        textAlign = TextAlign.Start
    )
}


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        ColorPickerButton(
            colorPickerModal = rememberIvyModal(),
            selectedColor = UI.colors.primary
        )
    }
}
// endregion
