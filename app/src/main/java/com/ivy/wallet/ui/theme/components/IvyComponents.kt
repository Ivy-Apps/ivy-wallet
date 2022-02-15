package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI


@Composable
fun ActionsRow(
    modifier: Modifier = Modifier,
    lineColor: Color = UI.colors.medium,
    Content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val height = this.size.height
                val width = this.size.width

                drawLine(
                    color = lineColor,
                    strokeWidth = 2.dp.toPx(),
                    start = Offset(
                        x = 0f,
                        y = height / 2
                    ),
                    end = Offset(
                        x = width,
                        y = height / 2
                    )
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Content()
    }
}