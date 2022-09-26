package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivy.design.util.thenIf

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