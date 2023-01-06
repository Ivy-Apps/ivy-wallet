package com.ivy.transaction.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Orange
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.transaction.R

@Composable
internal fun TrnTimeComponent(
    extendedTrnTime: TrnTimeUi,
    modifier: Modifier = Modifier,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
) {
    val feeling = when (extendedTrnTime) {
        is TrnTimeUi.Actual -> Feeling.Positive
        is TrnTimeUi.Due -> Feeling.Custom(Orange)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyButton(
            modifier = Modifier.weight(1.5f),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = feeling,
            text = when (extendedTrnTime) {
                is TrnTimeUi.Actual -> extendedTrnTime.actualDate
                is TrnTimeUi.Due -> extendedTrnTime.dueOnDate
            },
            icon = R.drawable.ic_round_calendar_month_24,
            typo = UI.typoSecond.b2,
            onClick = onDateClick
        )
        SpacerHor(width = 8.dp)
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = feeling,
            text = when (extendedTrnTime) {
                is TrnTimeUi.Actual -> extendedTrnTime.actualTime
                is TrnTimeUi.Due -> extendedTrnTime.dueOnTime
            },
            icon = R.drawable.round_time_24,
            typo = UI.typoSecond.b2,
            onClick = onTimeClick
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview_Actual() {
    ComponentPreview {
        TrnTimeComponent(
            extendedTrnTime = TrnTimeUi.Actual(
                actualDate = "Dec 21, 2021",
                actualTime = "21:46",
            ),
            onDateClick = {},
            onTimeClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Due() {
    ComponentPreview {
        TrnTimeComponent(
            extendedTrnTime = TrnTimeUi.Due(
                dueOnDate = "Dec 21, 2021",
                dueOnTime = "01:46 pm",
                upcoming = true
            ),
            onDateClick = {},
            onTimeClick = {},
        )
    }
}
// endregion