package com.ivy.core.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.core.domain.pure.format.dummyFormattedValue
import com.ivy.core.ui.data.transaction.TrnListItemUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.B1
import com.ivy.design.l2_components.B2Second
import com.ivy.design.l2_components.C
import com.ivy.design.util.ComponentPreview

@Composable
fun TrnListItemUi.DateDivider.DateDivider() {
    Row(
        modifier = Modifier
            .padding(start = 24.dp, end = 32.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Date(date = date, day = day)
        Cashflow(cashflow = cashflow, positiveCashflow = positiveCashflow)
    }
}

@Composable
private fun RowScope.Date(
    date: String,
    day: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        date.B1(fontWeight = FontWeight.ExtraBold)
        SpacerVer(height = 4.dp)
        day.C(fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Cashflow(
    cashflow: FormattedValue,
    positiveCashflow: Boolean,
) {
    "${cashflow.amount} ${cashflow.currency}".B2Second(
        color = if (positiveCashflow) UI.colors.green else UI.colors.neutral
    )
}

// region Previews
@Preview
@Composable
private fun Preview_Positive() {
    ComponentPreview {
        TrnListItemUi.DateDivider(
            date = "September 25.",
            day = "Today",
            cashflow = dummyFormattedValue("154.32"),
            positiveCashflow = true
        ).DateDivider()
    }
}

@Preview
@Composable
private fun Preview_Negative() {
    ComponentPreview {
        TrnListItemUi.DateDivider(
            date = "September 25. 2020",
            day = "Today",
            cashflow = dummyFormattedValue("-1k"),
            positiveCashflow = false
        ).DateDivider()
    }
}
// endregion