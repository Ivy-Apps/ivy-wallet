package com.ivy.design.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.H1
import com.ivy.design.l1_buildingBlocks.SpacerVer

@Composable
fun ScreenPlaceholder(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UI.colors.pure),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        H1(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = text,
            textAlign = TextAlign.Center
        )
        SpacerVer(height = 4.dp)
        B1(
            text = "Work in progress...",
            fontWeight = FontWeight.Bold,
            color = UI.colors.orange,
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        ScreenPlaceholder(text = "Preview")
    }
}
// endregion