package com.ivy.wallet.ui.charts.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.domain.pure.charts.SingleChartPoint
import com.ivy.wallet.ui.charts.toValues2
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.charts.linechart.Function
import com.ivy.wallet.ui.theme.components.charts.linechart.IvyLineChart
import com.ivy.wallet.utils.format
import java.text.DecimalFormat

fun LazyListScope.walletCharts(
    period: ChartPeriod,
    baseCurrencyCode: String,
    balanceChart: List<SingleChartPoint>,
) {
    item {
        Spacer(Modifier.height(32.dp))

        BalanceChart(
            balanceChart = balanceChart,
            period = period,
            baseCurrencyCode = baseCurrencyCode
        )
    }

//    item {
//        Spacer(Modifier.height(48.dp))
//
//        Text(
//            modifier = Modifier.padding(start = 24.dp),
//            text = "Income & Expense chart",
//            style = Typo.body1
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        val incomeFunction = Function(
//            values = incomeValues.toValue(),
//            color = Green
//        )
//        val expenseFunction = Function(
//            values = expenseValues.toValue(),
//            color = Red
//        )
//        val functions = listOf(incomeFunction, expenseFunction)
//
//        var tapEvent: TapEvent? by remember {
//            mutableStateOf(null)
//        }
//
//        IvyLineChart(
//            modifier = Modifier.padding(horizontal = 24.dp),
//            functions = functions,
//            xLabel = {
//                val range = balanceChart.getOrNull(it.toInt())?.range ?: return@IvyLineChart ""
//                period.xLabel(range)
//            },
//            yLabel = {
//                it.format(baseCurrencyCode)
//            },
//            onTap = {
//                tapEvent = it
//            }
//        )
//
//        tapEvent?.let {
//            Spacer(Modifier.height(16.dp))
//
//            ChartInfoCard(
//                baseCurrencyCode = baseCurrencyCode,
//                backgroundColor = functions[it.functionIndex].color,
//                chartPoint = if (it.functionIndex == 0)
//                    incomeValues[it.valueIndex] else expenseValues[it.valueIndex]
//            )
//        }
//    }

    item {
        Spacer(Modifier.height(196.dp)) //scroll hack
    }
}

@Composable
fun BalanceChart(
    balanceChart: List<SingleChartPoint>,
    period: ChartPeriod,
    baseCurrencyCode: String
) {
    var tappedPoint: SingleChartPoint? by remember {
        mutableStateOf(null)
    }

    Text(
        modifier = Modifier.padding(start = 24.dp),
        text = stringResource(R.string.balance_chart),
        style = UI.typo.b1
    )

    Spacer(Modifier.height(16.dp))

    val values = balanceChart.toValues2()

    IvyLineChart(
        modifier = Modifier.padding(horizontal = 8.dp),
        height = 400.dp,
        title = stringResource(R.string.balance, period.display().uppercase()),
        functions = listOf(
            Function(
                values = values,
                color = Green,
                colorDown = Red
            )
        ),
        xLabel = {
            period.xLabel(range = balanceChart[it.toInt()].range)
        },
        yLabel = {
            DecimalFormat("#,###").format(it)
        },
        onTap = {
            tappedPoint = balanceChart[it.valueIndex]
        }
    )

    tappedPoint?.let {
        Spacer(Modifier.height(16.dp))

        ChartInfoCard(
            baseCurrencyCode = baseCurrencyCode,
            backgroundColor = Ivy,
            chartPoint = it
        )
    }
}

@Composable
fun ChartInfoCard(
    baseCurrencyCode: String,
    backgroundColor: Color,
    chartPoint: SingleChartPoint,
    formatValueAsCount: Boolean = false,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(backgroundColor, UI.shapes.r2)
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            text = chartPoint.range.toFromToRange().toDisplay(),
            style = UI.typo.b2.style(
                color = White
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = if (formatValueAsCount) {
                chartPoint.value.toInt().toString()
            } else {
                "${chartPoint.value.toDouble().format(baseCurrencyCode)} $baseCurrencyCode"
            },
            style = UI.typo.nB2.style(
                color = White
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}
