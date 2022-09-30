package com.ivy.design.l2_components.modal.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l2_components.modal.scope.ModalScope

@Suppress("unused")
@Composable
fun ModalScope.Body(text: String) {
    B2(
        text = text,
        modifier = Modifier.padding(horizontal = 32.dp),
        fontWeight = FontWeight.Medium,
    )
}