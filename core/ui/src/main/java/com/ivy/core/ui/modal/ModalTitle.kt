package com.ivy.core.ui.modal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.temp.Preview
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerVer

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
    Preview {
        Modal(modal = modal, Actions = {}) {
            ModalTitle(text = "Title")
            SpacerVer(height = 32.dp)
        }
    }
}