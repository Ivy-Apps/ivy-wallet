package com.ivy.wallet.ui.planned.edit

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
import com.ivy.wallet.base.uppercaseLocal
import com.ivy.wallet.model.IntervalType
import com.ivy.wallet.ui.theme.IvyComponentPreview

import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Shapes
import com.ivy.wallet.ui.theme.components.AddPrimaryAttributeButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import java.time.LocalDateTime

@Composable
fun RecurringRule(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
    onShowRecurringRuleModal: () -> Unit,
) {
    if (
        hasRecurringRule(
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime
        )
    ) {
        RecurringRuleCard(
            startDate = startDate!!,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime,
            onClick = {
                onShowRecurringRuleModal()
            }
        )
    } else {
        AddPrimaryAttributeButton(
            icon = R.drawable.ic_planned_payments,
            text = "Add planned date of payment",
            onClick = onShowRecurringRuleModal
        )
    }
}

fun hasRecurringRule(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
): Boolean {
    return startDate != null &&
            ((intervalN != null && intervalType != null) || oneTime)
}

@Composable
private fun RecurringRuleCard(
    startDate: LocalDateTime,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(Shapes.rounded16)
            .background(UI.colors.medium, Shapes.rounded16)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(icon = R.drawable.ic_planned_payments)

        Spacer(Modifier.width(8.dp))

        Column {
            Text(
                text = if (oneTime) "Planned for" else "Planned start at",
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            if (!oneTime && intervalType != null && intervalN != null) {
                Spacer(Modifier.height(4.dp))

                val intervalTypeLabel = intervalType.forDisplay(intervalN).uppercaseLocal()
                Text(
                    text = "REPEATS EVERY $intervalN $intervalTypeLabel",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Orange
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = startDate.toLocalDate().formatDateOnly(),
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview_Empty() {
    IvyComponentPreview {
        RecurringRule(
            startDate = null,
            intervalN = null,
            intervalType = null,
            oneTime = true
        ) {
        }
    }
}

@Preview
@Composable
private fun Preview_Repeat() {
    IvyComponentPreview {
        RecurringRule(
            startDate = timeNowUTC(),
            intervalN = 1,
            intervalType = IntervalType.MONTH,
            oneTime = false
        ) {
        }
    }
}

@Preview
@Composable
private fun Preview_OneTime() {
    IvyComponentPreview {
        RecurringRule(
            startDate = timeNowUTC().plusDays(5),
            intervalN = null,
            intervalType = null,
            oneTime = true
        ) {
        }
    }
}