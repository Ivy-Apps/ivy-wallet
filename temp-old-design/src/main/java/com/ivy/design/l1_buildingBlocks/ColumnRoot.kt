package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivy.design.utils.thenIf

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ColumnRoot(
    modifier: Modifier = Modifier,
    statusBarPadding: Boolean = true,
    navigationBarsPadding: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    Content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .thenIf(statusBarPadding) {
                statusBarsPadding()
            }
            .thenIf(navigationBarsPadding) {
                navigationBarsPadding()
            },
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        Content()
    }
}
