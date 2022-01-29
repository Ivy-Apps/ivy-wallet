package com.ivy.wallet.ui.charts.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.format
import com.ivy.wallet.ui.charts.ChartPeriod
import com.ivy.wallet.ui.charts.TimeValue
import com.ivy.wallet.ui.charts.toValues
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.charts.Function
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart

fun LazyListScope.generalCharts(
    period: ChartPeriod,
    baseCurrencyCode: String,
    balanceValues: List<TimeValue>,
    incomeValues: List<TimeValue>,
    expenseValues: List<TimeValue>,
) {
    item {
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

        val values = balanceValues.toValues()

        IvyLineChart(
            modifier = Modifier.padding(horizontal = 24.dp),
            functions = listOf(
                Function(
                    values = values,
                    color = Green,
                    colorDown = Red
                )
            ),
            xLabel = {
                period.xLabel(range = balanceValues[it.toInt()].range)
            },
            yLabel = {
                it.format(baseCurrencyCode)
            },
            onTap = {
                balanceTapped = balanceValues[it.valueIndex]
            }
        )

        if (balanceTapped != null) {
            Spacer(Modifier.height(16.dp))

            ChartInfoCard(
                baseCurrencyCode = baseCurrencyCode,
                backgroundColor = Ivy,
                timeValue = balanceTapped!!
            )
        }
    }

    item {
        Spacer(Modifier.height(48.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = "Income & Expense chart",
            style = Typo.body1
        )

        Spacer(Modifier.height(16.dp))

        val incomeFunction = Function(
            values = incomeValues.toValues(),
            color = Green
        )
        val expenseFunction = Function(
            values = expenseValues.toValues(),
            color = Red
        )

        IvyLineChart(
            modifier = Modifier.padding(horizontal = 24.dp),
            functions = listOf(incomeFunction, expenseFunction),
            xLabel = {
                val range = balanceValues.getOrNull(it.toInt())?.range ?: return@IvyLineChart ""
                period.xLabel(range)
            },
            yLabel = {
                it.format(baseCurrencyCode)
            },
            onTap = {
                //TODO: Implement
            }
        )
    }

    item {
        Spacer(Modifier.height(196.dp)) //scroll hack
    }
}

@Composable
fun ChartInfoCard(
    baseCurrencyCode: String,
    backgroundColor: Color,
    timeValue: TimeValue
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(backgroundColor, Shapes.rounded24)
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            text = timeValue.range.toDisplay(),
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