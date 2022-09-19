package com.ivy.design.l2_components.modal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview

@Composable
fun ColumnScope.ModalTitle(
    text: String
) {
    SpacerVer(height = 24.dp)
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = text,
        style = UI.typo.b1.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Preview
@Composable
private fun Preview() {
    val modal = IvyModal()
    modal.show()
    IvyPreview {
        Modal(modal = modal, Actions = {}) {
            ModalTitle(text = "Title")
            SpacerVer(height = 32.dp)
        }
    }
}