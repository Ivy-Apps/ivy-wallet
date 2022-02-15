package com.ivy.wallet.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.base.formatNicelyWithTime
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Shapes
import com.ivy.wallet.ui.theme.components.IvyIcon
import java.time.LocalDateTime

@Composable
fun TransactionDateTime(
    dateTime: LocalDateTime?,
    dueDateTime: LocalDateTime?,

    onEditDateTime: () -> Unit
) {
    if (dueDateTime == null || dateTime != null) {
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(Shapes.rounded16)
                .background(IvyTheme.colors.medium, Shapes.rounded16)
                .clickable {
                    onEditDateTime()
                }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            IvyIcon(icon = R.drawable.ic_calendar)

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Created on",
                style = UI.typo.b2.style(
                    color = IvyTheme.colors.gray,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.width(24.dp))
            Spacer(Modifier.weight(1f))

            Text(
                text = (dateTime ?: timeNowUTC()).formatNicelyWithTime(
                    noWeekDay = true
                ),
                style = UI.typo.nB2.style(
                    color = IvyTheme.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyComponentPreview {
        TransactionDateTime(
            dateTime = timeNowUTC(),
            dueDateTime = null
        ) {

        }
    }
}