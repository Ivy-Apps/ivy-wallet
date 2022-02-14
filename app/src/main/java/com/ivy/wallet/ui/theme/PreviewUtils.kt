package com.ivy.wallet.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.ui.IvyAppPreview


@Composable
fun IvyComponentPreview(
    theme: Theme = Theme.LIGHT,
    Content: @Composable BoxScope.() -> Unit
) {
    IvyAppPreview(
        theme = theme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(IvyTheme.colors.pure),
            contentAlignment = Alignment.Center
        ) {
            Content()
        }
    }
}