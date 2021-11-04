package com.ivy.wallet.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.ui.theme.*

@Composable
fun DataCircle(
    count: Int,
    metric: String,
    circleColor: Color,
    parentCount: Int?,
    avgCount: Double? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .background(circleColor, CircleShape)
                .padding(all = 24.dp),
            text = count.toString(),
            style = Typo.numberH1.style(
                color = findContrastTextColor(circleColor),
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = metric,
            style = Typo.body1.style(
                textAlign = TextAlign.Center
            )
        )

        if (parentCount != null && parentCount != 0) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "${(count / parentCount.toDouble()).times(100).format(2)}%",
                style = Typo.numberBody2.colorAs(IvyTheme.colors.mediumInverse)
            )
        }

        if (avgCount != null) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "AVG ${avgCount.format(2)}",
                style = Typo.numberCaption.colorAs(IvyTheme.colors.mediumInverse)
            )
        }
    }

}