package com.ivy.wallet.ui.charts

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.charts.types.AccountCharts
import com.ivy.wallet.ui.charts.types.CategoryCharts
import com.ivy.wallet.ui.charts.types.GeneralCharts
import com.ivy.wallet.ui.ivyContext
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.IvyToolbar

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
        Toolbar()

        Spacer(Modifier.height(8.dp))

        var period by remember {
            mutableStateOf(Period.LAST_12_MONTHS)
        }

        Period(
            period = period,
            onSetPeriod = {
                period = it
            }
        )

        var chartType by remember {
            mutableStateOf(ChartType.GENERAL)
        }

        Spacer(Modifier.height(12.dp))

        ChartsType(
            selectedChartType = chartType,
            onSetChartType = {
                chartType = it
            }
        )

        Spacer(Modifier.height(4.dp))

        IvyDividerLine()

        when (chartType) {
            ChartType.GENERAL -> GeneralCharts(
                period = period,
                baseCurrencyCode = baseCurrencyCode,
                balanceValues = balanceValues
            )
            ChartType.CATEGORY -> CategoryCharts(
                period = period
            )
            ChartType.ACCOUNT -> AccountCharts(
                period = period
            )
        }
    }
}

@Composable
private fun Toolbar() {
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
}

@Composable
private fun Period(
    period: Period,
    onSetPeriod: (Period) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        Text(
            modifier = Modifier.clickable {
                //TODO: handle click
            },
            text = "Period:",
            style = Typo.body1
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier
                .clip(Shapes.roundedFull)
                .border(1.dp, IvyTheme.colors.mediumInverse, Shapes.roundedFull)
                .clickable {
                    //TODO: handle click
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = period.display(),
            style = Typo.body2.style(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun ChartsType(
    selectedChartType: ChartType,
    onSetChartType: (ChartType) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChartType.values().forEach {
            ChartButton(
                modifier = Modifier.weight(1f),
                chartType = it,
                selected = it == selectedChartType
            ) {
                onSetChartType(it)
            }
        }

    }
}

@Composable
private fun ChartButton(
    modifier: Modifier = Modifier,
    chartType: ChartType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp),
        text = chartType.display(),
        style = Typo.body2.style(
            color = if (selected) Ivy else IvyTheme.colors.pureInverse,
            textAlign = TextAlign.Center
        )
    )
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