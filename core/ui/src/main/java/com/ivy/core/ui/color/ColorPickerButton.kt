package com.ivy.core.ui.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l0_system.color.toHex
import com.ivy.design.l1_buildingBlocks.B2Second
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.ComponentPreview

@Composable
fun ColorPickerButton(
    colorPickerModal: IvyModal,
    selectedColor: Color,
    modifier: Modifier = Modifier,
) {
    val colorHex = remember(selectedColor) { selectedColor.toHex() }
    B2Second(
        modifier = modifier
            .clip(UI.shapes.rounded)
            .background(selectedColor, UI.shapes.rounded)
            .clickable { colorPickerModal.show() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        text = "#$colorHex",
        fontWeight = FontWeight.ExtraBold,
        color = rememberDynamicContrast(selectedColor),
        textAlign = TextAlign.Center
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
