package com.ivy.wallet.ui.component.transaction

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI


@Composable
fun TransactionsDividerLine(
    modifier: Modifier = Modifier,
    paddingHorizontal: Dp = 24.dp
) {
    Divider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal),
        color = UI.colors.medium,
        thickness = 2.dp
    )
}