package com.ivy.design.l2_components.modal.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.util.IvyPreview

@Suppress("unused")
@Composable
fun ModalScope.Title(
    text: String,
    paddingStart: Dp = 32.dp,
    color: Color = UI.colorsInverted.pure
) {
    B1(
        text = text,
        modifier = Modifier
            .padding(start = paddingStart)
            .padding(top = 24.dp),
        fontWeight = FontWeight.ExtraBold,
        color = color
    )
}

@Preview
@Composable
private fun Preview() {
    val modal = IvyModal()
    modal.show()
    IvyPreview {
        Modal(modal = modal, actions = {}) {
            Title(text = "Title")
            SpacerVer(height = 32.dp)
        }
    }
}