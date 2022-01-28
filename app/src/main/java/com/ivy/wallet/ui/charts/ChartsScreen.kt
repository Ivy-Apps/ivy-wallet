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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.charts.types.accountCharts
import com.ivy.wallet.ui.charts.types.categoryCharts
import com.ivy.wallet.ui.charts.types.generalCharts
import com.ivy.wallet.ui.ivyContext
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.IvyToolbar

@Composable
fun BoxWithConstraintsScope.ChartsScreen(screen: Screen.Charts) {
    val viewModel: ChartsViewModel = viewModel()

    val period by viewModel.period.collectAsState()
    val baseCurrencyCode by viewModel.baseCurrencyCode.collectAsState()
    val balanceValues by viewModel.balanceValues.collectAsState()
    val incomeValues by viewModel.incomeValues.collectAsState()
    val expenseValues by viewModel.expenseValues.collectAsState()
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
        balanceValues = balanceValues,
        incomeValues = incomeValues,
        expenseValues = expenseValues,
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
    balanceValues: List<TimeValue> = emptyList(),
    incomeValues: List<TimeValue> = emptyList(),
    expenseValues: List<TimeValue> = emptyList(),
    categories: List<Category> = emptyList(),

    categoryExpenseValues: Map<Category, List<TimeValue>> = emptyMap(),
    categoryExpenseCount: Map<Category, List<TimeValue>> = emptyMap(),
    categoryIncomeValues: Map<Category, List<TimeValue>> = emptyMap(),
    categoryIncomeCount: Map<Category, List<TimeValue>> = emptyMap(),

    onLoadCategory: (Category) -> Unit = {},
    onRemoveCategory: (Category) -> Unit = {},
    onChangePeriod: (ChartPeriod) -> Unit = {}
) {
    var chartType by remember {
        mutableStateOf(ChartType.GENERAL)
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
            ChartType.GENERAL -> generalCharts(
                period = period,
                baseCurrencyCode = baseCurrencyCode,
                balanceValues = balanceValues,
                incomeValues = incomeValues,
                expenseValues = expenseValues
            )
            ChartType.CATEGORY -> categoryCharts(
                period = period,
                baseCurrencyCode = baseCurrencyCode,
                categories = categories,

                categoryExpenseValues = categoryExpenseValues,
                categoryExpenseCount = categoryExpenseCount,
                categoryIncomeValues = categoryIncomeValues,
                categoryIncomeCount = categoryIncomeCount,

                onLoadCategory = onLoadCategory,
                onRemoveCategory = onRemoveCategory
            )
            ChartType.ACCOUNT -> accountCharts(
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
            text = "Period:",
            style = Typo.body1
        )

        Spacer(Modifier.width(12.dp))

        Text(
            modifier = Modifier
                .clip(Shapes.roundedFull)
                .border(1.dp, IvyTheme.colors.mediumInverse, Shapes.roundedFull)
                .clickable {
                    togglePeriod()
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
            period = ChartPeriod.LAST_12_MONTHS,
            baseCurrencyCode = "BGN",
        )
    }
}