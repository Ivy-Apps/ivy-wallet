package com.ivy.wallet.ui.charts.types

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.ui.charts.MonthValue
import com.ivy.wallet.ui.charts.Period
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.Value

@Composable
fun GeneralCharts(
    period: Period,
    baseCurrencyCode: String,
    balanceValues: List<MonthValue>
) {
    Spacer(Modifier.height(16.dp))

    var balanceTapped: MonthValue? by remember {
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
            balanceValues[it.toInt()].month.month.name.first().uppercase()
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

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = "Balance ${balanceTapped!!.month.month.name}: ${
                balanceTapped!!.value.format(
                    baseCurrencyCode
                )
            } $baseCurrencyCode",
            style = Typo.numberBody1
        )
    }
}