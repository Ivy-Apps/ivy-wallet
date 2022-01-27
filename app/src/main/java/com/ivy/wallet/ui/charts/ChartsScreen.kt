package com.ivy.wallet.ui.charts

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.base.format
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.ivyContext
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.Value
import com.ivy.wallet.ui.theme.style

@Composable
fun BoxWithConstraintsScope.ChartsScreen(screen: Screen.Charts) {
    val viewModel: ChartsViewModel = viewModel()

    val baseCurrencyCode by viewModel.baseCurrencyCode.collectAsState()
    val balanceValues by viewModel.balanceValues.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        baseCurrencyCode = baseCurrencyCode,
        balanceValues = balanceValues
    )
}

@Composable
private fun UI(
    baseCurrencyCode: String,
    balanceValues: List<MonthValue> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val ivyContext = ivyContext()

        IvyToolbar(
            onBack = {
                ivyContext.back()
            }
        ) {
            Spacer(Modifier.width(32.dp))

            Text(
                text = "Charts",
                style = Typo.h2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        var balanceTapped: MonthValue? by remember {
            mutableStateOf(null)
        }

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
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        UI(
            baseCurrencyCode = "BGN"
        )
    }
}