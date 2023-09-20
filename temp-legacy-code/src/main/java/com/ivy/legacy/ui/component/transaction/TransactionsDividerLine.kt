package com.ivy.legacy.ui.component.transaction

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI

@Deprecated("Old design system. Use `:ivy-design` and Material3")
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
