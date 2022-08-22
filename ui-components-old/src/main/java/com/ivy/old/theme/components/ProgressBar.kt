package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.*


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    notFilledColor: Color = UI.colors.pure,
    positiveProgress: Boolean = true,
    percent: Double
) {
    Spacer(
        modifier = modifier
            .clip(UI.shapes.r4)
            .background(notFilledColor)
            .drawBehind {
                drawRect(
                    color = when {
                        percent <= 0.25 -> {
                            if (positiveProgress) Red else Green
                        }
                        percent <= 0.50 -> {
                            if (positiveProgress) Orange else Ivy
                        }
                        percent <= 0.75 -> {
                            if (positiveProgress) Ivy else Orange
                        }
                        else -> if (positiveProgress) Green else Red
                    },
                    size = size.copy(
                        width = (size.width * percent).toFloat()
                    )
                )
            },
    )
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.ComponentPreview {
        ProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp),
            notFilledColor = Gray,
            percent = 0.6
        )
    }
}