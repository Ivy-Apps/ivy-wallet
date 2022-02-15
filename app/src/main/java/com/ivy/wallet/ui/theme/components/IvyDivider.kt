package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.Shapes


@Composable
fun IvyDividerLine(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(UI.colors.medium)
    )
}

@Composable
fun IvyDividerLineRounded(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(UI.colors.medium, Shapes.roundedFull)
    )
}

@Preview
@Composable
private fun Preview() {
    IvyComponentPreview {
        IvyDividerLine()
    }
}