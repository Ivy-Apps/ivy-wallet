package com.ivy.core.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.common.dateNowUTC
import com.ivy.common.formatLocal
import com.ivy.core.domain.pure.dummy.dummyValue
import com.ivy.core.functions.transaction.dummyDateDivider
import com.ivy.core.ui.value.formatAmount
import com.ivy.data.Value
import com.ivy.data.transaction.TrnListItem
import com.ivy.design.l0_system.Gray
import com.ivy.design.l0_system.Green
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.ComponentPreview
import java.time.LocalDate

@Composable
fun TrnListItem.DateDivider.DateDivider() {
    Row(
        modifier = Modifier
            .padding(start = 24.dp, end = 32.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Date(date = date)
        Cashflow(cashflow = cashflow)
    }
}

@Composable
private fun RowScope.Date(
    date: LocalDate
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        val today = remember { LocalDate.now() }
        // TODO: Optimization: Move to GroupTrnsAct
        val formattedDate = remember(date) {
            date.formatLocal(if (today.year == date.year) "MMMM dd." else "MMM dd. yyy")
        }

        Text(
            text = formattedDate,
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold
            )
        )
        SpacerVer(height = 4.dp)

        // TODO: Optimization: Move to GroupTrnsAct
        val dateText = when (date) {
            today -> stringResource(R.string.today)
            today.minusDays(1) -> stringResource(R.string.yesterday)
            today.plusDays(1) -> stringResource(R.string.tomorrow)
            else -> null
        } ?: remember(date) { date.formatLocal("EEEE") }

        Text(
            text = dateText,
            style = UI.typo.c.style(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun Cashflow(
    cashflow: Value
) {
    val formattedCashFlow = cashflow.formatAmount(shortenBigNumbers = false)
    Text(
        text = "$formattedCashFlow ${cashflow.currency}",
        style = UI.typo.nB2.style(
            fontWeight = FontWeight.Bold,
            color = if (cashflow.amount > 0) Green else Gray
        )
    )
}

// region Previews
@Preview
@Composable
private fun Preview_Today() {
    ComponentPreview {
        dummyDateDivider(
            date = dateNowUTC(),
            cashflow = dummyValue(154.32)
        ).DateDivider()
    }
}

@Preview
@Composable
private fun Preview_Yesterday() {
    ComponentPreview {
        dummyDateDivider(
            date = dateNowUTC().minusDays(1),
            cashflow = dummyValue(-1_000.23)
        ).DateDivider()
    }
}

@Preview
@Composable
private fun Preview_OneYear_Ago() {
    ComponentPreview {
        dummyDateDivider(
            date = dateNowUTC().minusYears(1),
            cashflow = dummyValue(-132.0)
        ).DateDivider()
    }
}
// endregion