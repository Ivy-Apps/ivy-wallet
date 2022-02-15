package com.ivy.wallet.ui.edit.core

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
import com.ivy.wallet.base.formatDateOnly
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Shapes
import com.ivy.wallet.ui.theme.components.IvyIcon
import java.time.LocalDateTime

@Composable
fun DueDate(
    dueDate: LocalDateTime,
    onPickDueDate: () -> Unit,
) {
    DueDateCard(
        dueDate = dueDate,
        onClick = {
            onPickDueDate()
        }
    )
}

@Composable
private fun DueDateCard(
    dueDate: LocalDateTime,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Shapes.rounded16)
            .background(IvyTheme.colors.medium, Shapes.rounded16)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(icon = R.drawable.ic_planned_payments)

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Planned for",
            style = UI.typo.b2.style(
                fontWeight = FontWeight.ExtraBold,
                color = IvyTheme.colors.pureInverse
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = dueDate.toLocalDate().formatDateOnly(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview_OneTime() {
    IvyComponentPreview {
        DueDate(
            dueDate = timeNowUTC().plusDays(5),
        ) {
        }
    }
}