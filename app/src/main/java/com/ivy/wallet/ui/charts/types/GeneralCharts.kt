package com.ivy.wallet.ui.charts.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.ui.charts.Period
import com.ivy.wallet.ui.charts.TimeValue
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.Value

@Composable
fun GeneralCharts(
    period: Period,
    baseCurrencyCode: String,
    balanceValues: List<TimeValue>
) {
    Spacer(Modifier.height(16.dp))

    var balanceTapped: TimeValue? by remember {
        mutableStateOf(null)
    }

    Text(
        modifier = Modifier.padding(start = 24.dp),
        text = "Balance chart",
        style = Typo.body1
    )

    Spacer(Modifier.height(16.dp))

    IvyLineChart(
        modifier = Modifier.padding(horizontal = 24.dp),
        values = balanceValues.mapIndexed { index, it ->
            Value(
                x = index.toDouble(),
                y = it.value
            )
        },
        xLabel = {
            balanceValues[it.toInt()].dateTime.month.name.first().uppercase()
        },
        yLabel = {
            it.format(baseCurrencyCode)
        },
        onTap = {
            balanceTapped = balanceValues[it]
        }
    )

    if (balanceTapped != null) {
        Spacer(Modifier.height(16.dp))

        BalanceChartInfoCard(
            baseCurrencyCode = baseCurrencyCode,
            timeValue = balanceTapped!!
        )
    }
}

@Composable
fun BalanceChartInfoCard(
    baseCurrencyCode: String,
    timeValue: TimeValue
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(Ivy, Shapes.rounded24)
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            text = timeValue.dateTime.format("MMMM, yyyy"),
            style = Typo.body2.style(
                color = White
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "${timeValue.value.format(baseCurrencyCode)} $baseCurrencyCode",
            style = Typo.numberBody2.style(
                color = White
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}