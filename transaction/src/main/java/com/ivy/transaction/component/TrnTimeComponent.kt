package com.ivy.transaction.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.design.l0_system.color.Orange
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.transaction.R

@Composable
internal fun TrnTimeComponent(
    trnTime: TrnTimeUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = when (trnTime) {
            is TrnTimeUi.Actual -> Feeling.Positive
            is TrnTimeUi.Due -> Feeling.Custom(Orange)
        },
        text = when (trnTime) {
            is TrnTimeUi.Actual -> trnTime.actual
            is TrnTimeUi.Due -> "Due on ${trnTime.dueOn}"
        },
        icon = R.drawable.ic_date,
        onClick = onClick
    )
}


// region Preview
@Preview
@Composable
private fun Preview_Actual() {
    ComponentPreview {
        TrnTimeComponent(
            trnTime = TrnTimeUi.Actual("Dec 21, 2021"),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Due() {
    ComponentPreview {
        TrnTimeComponent(
            trnTime = TrnTimeUi.Due(dueOn = "Dec 21, 2021", upcoming = true),
            onClick = {}
        )
    }
}
// endregion