package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.design.l0_system.UI


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
            .background(UI.colors.medium, UI.shapes.rFull)
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletComponentPreview {
        IvyDividerLine()
    }
}