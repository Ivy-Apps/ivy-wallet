package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.ui.theme.GradientRed
import com.ivy.wallet.ui.theme.White

@Composable
fun DeleteButton(
    onClick: () -> Unit,
) {
    IvyCircleButton(
        modifier = Modifier
            .size(48.dp),
        backgroundPadding = 6.dp,
        icon = R.drawable.ic_delete,
        backgroundGradient = GradientRed,
        enabled = true,
        tint = White,
        onClick = onClick
    )
}