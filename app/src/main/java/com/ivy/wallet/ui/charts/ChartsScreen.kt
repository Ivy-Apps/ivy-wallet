package com.ivy.wallet.ui.charts


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.domain.pure.charts.IncomeExpenseChartPoint
import com.ivy.wallet.domain.pure.charts.SingleChartPoint
import com.ivy.wallet.ui.Charts
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.charts.charts.accountCharts
import com.ivy.wallet.ui.charts.charts.walletCharts
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.utils.onScreenStart

@Composable
fun BoxWithConstraintsScope.ChartsScreen(screen: Charts) {
    val viewModel: ChartsViewModel = viewModel()

    val period by viewModel.period.collectAsState()
    val baseCurrencyCode by viewModel.baseCurrencyCode.collectAsState()

    val balanceChart by viewModel.balanceChart.collectAsState()
    val incomeExpenseChart by viewModel.incomeExpenseChart.collectAsState()

    val categories by viewModel.categories.collectAsState()

    val categoryExpenseValues by viewModel.categoryExpenseValues.collectAsState()
    val categoryExpenseCount by viewModel.categoryExpenseCount.collectAsState()
    val categoryIncomeValues by viewModel.categoryIncomeValues.collectAsState()
    val categoryIncomeCount by viewModel.categoryIncomeCount.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        period = period,
        baseCurrencyCode = baseCurrencyCode,

        balanceChart = balanceChart,
        incomeExpenseChart = incomeExpenseChart,

        categories = categories,

        categoryExpenseValues = categoryExpenseValues,
        categoryExpenseCount = categoryExpenseCount,
        categoryIncomeValues = categoryIncomeValues,
        categoryIncomeCount = categoryIncomeCount,

        onLoadCategory = viewModel::loadValuesForCategory,
        onRemoveCategory = viewModel::removeCategory,
        onChangePeriod = viewModel::changePeriod
    )
}

@Composable
private fun UI(
    period: ChartPeriod,
    baseCurrencyCode: String,

    balanceChart: List<SingleChartPoint> = emptyList(),
    incomeExpenseChart: List<IncomeExpenseChartPoint> = emptyList(),

    categories: List<Category> = emptyList(),

    categoryExpenseValues: List<CategoryValues> = emptyList(),
    categoryExpenseCount: List<CategoryValues> = emptyList(),
    categoryIncomeValues: List<CategoryValues> = emptyList(),
    categoryIncomeCount: List<CategoryValues> = emptyList(),

    onLoadCategory: (Category) -> Unit = {},
    onRemoveCategory: (Category) -> Unit = {},
    onChangePeriod: (ChartPeriod) -> Unit = {}
) {
    var chartType by remember {
        mutableStateOf(ChartType.WALLET)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item {
            Toolbar()

            Spacer(Modifier.height(8.dp))

            Period(
                period = period,
                onSetPeriod = {
                    onChangePeriod(it)
                }
            )

            Spacer(Modifier.height(12.dp))

            ChartsType(
                selectedChartType = chartType,
                onSetChartType = {
                    chartType = it
                }
            )

            Spacer(Modifier.height(4.dp))

            IvyDividerLine()
        }

        when (chartType) {
            ChartType.WALLET -> walletCharts(
                period = period,
                baseCurrencyCode = baseCurrencyCode,
                balanceChart = balanceChart,
            )
            ChartType.CATEGORY -> {
                TODO()

//                categoryCharts(
//                    period = period,
//                    baseCurrencyCode = baseCurrencyCode,
//                    categories = categories,
//
//                    categoryExpenseValues = categoryExpenseValues,
//                    categoryExpenseCount = categoryExpenseCount,
//                    categoryIncomeValues = categoryIncomeValues,
//                    categoryIncomeCount = categoryIncomeCount,
//
//                    onLoadCategory = onLoadCategory,
//                    onRemoveCategory = onRemoveCategory
//                )
            }
            ChartType.ACCOUNT -> accountCharts(
                period = period
            )
        }
    }
}

@Composable
private fun Toolbar() {
    val nav = navigation()

    IvyToolbar(
        onBack = {
            nav.back()
        }
    ) {
        Spacer(Modifier.width(32.dp))

        Text(
            text = stringResource(R.string.charts),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Composable
private fun Period(
    period: ChartPeriod,
    onSetPeriod: (ChartPeriod) -> Unit
) {
    val togglePeriod = {
        onSetPeriod(
            when (period) {
                ChartPeriod.LAST_12_MONTHS -> ChartPeriod.LAST_6_MONTHS
                ChartPeriod.LAST_6_MONTHS -> ChartPeriod.LAST_4_WEEKS
                ChartPeriod.LAST_4_WEEKS -> ChartPeriod.LAST_7_DAYS
                ChartPeriod.LAST_7_DAYS -> ChartPeriod.LAST_12_MONTHS
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        Text(
            modifier = Modifier.clickable {
                togglePeriod()
            },
            text = stringResource(R.string.period),
            style = UI.typo.b1
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier
                .clip(UI.shapes.rFull)
                .border(1.dp, UI.colors.mediumInverse, UI.shapes.rFull)
                .clickable {
                    togglePeriod()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = period.display(),
            style = UI.typo.b2.style(
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
        style = UI.typo.b2.style(
            color = if (selected) Ivy else UI.colors.pureInverse,
            textAlign = TextAlign.Center
        )
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            period = ChartPeriod.LAST_12_MONTHS,
            baseCurrencyCode = "BGN",
        )
    }
}
