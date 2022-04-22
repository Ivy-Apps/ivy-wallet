package com.ivy.wallet.ui.charts.charts

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.charts.CategoryValues
import com.ivy.wallet.ui.charts.toValue
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRangeUnsafe
import com.ivy.wallet.ui.reports.ListItem
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.components.charts.linechart.Function
import com.ivy.wallet.ui.theme.components.charts.linechart.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.linechart.TapEvent
import com.ivy.wallet.ui.theme.toComposeColor
import com.ivy.wallet.utils.format

fun LazyListScope.categoryCharts(
    period: ChartPeriod,
    baseCurrencyCode: String,
    categories: List<Category>,

    categoryExpenseValues: List<CategoryValues> = emptyList(),
    categoryExpenseCount: List<CategoryValues> = emptyList(),
    categoryIncomeValues: List<CategoryValues> = emptyList(),
    categoryIncomeCount: List<CategoryValues> = emptyList(),

    onLoadCategory: (Category) -> Unit,
    onRemoveCategory: (Category) -> Unit
) {
    item {
        Spacer(Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.testTag("budget_categories_row")
        ) {
            item {
                Spacer(Modifier.width(24.dp))
            }


            items(items = categories) { category ->
                ListItem(
                    icon = category.icon,
                    defaultIcon = R.drawable.ic_custom_category_s,
                    text = category.name,
                    selectedColor = category.color.toComposeColor().takeIf {
                        categoryExpenseValues.any { it.category == category }
                    }
                ) { selected ->
                    if (selected) {
                        //remove category
                        onRemoveCategory(category)
                    } else {
                        //add category
                        onLoadCategory(category)
                    }
                }
            }

            item {
                Spacer(Modifier.width(24.dp))
            }
        }
    }

    item {
        CategoriesChart(
            period = period,
            title = stringRes(R.string.expenses),
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryExpenseValues,
            countChart = false
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = stringRes(R.string.expenses_count),
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryExpenseCount,
            countChart = true
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = stringRes(R.string.income),
            titleColor = Green,
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryIncomeValues,
            countChart = false
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = stringRes(R.string.income_count),
            titleColor = Green,
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryIncomeCount,
            countChart = true
        )
    }

    item {
        Spacer(Modifier.height(196.dp)) //scroll hack
    }
}

@Composable
private fun CategoriesChart(
    period: ChartPeriod,
    title: String,
    titleColor: Color = UI.colors.pureInverse,
    baseCurrencyCode: String,
    categoryValues: List<CategoryValues>,
    countChart: Boolean,
) {
    Spacer(Modifier.height(48.dp))

    val functions = categoryValues.map { entry ->
        Function(
            values = entry.values.toValue(),
            color = entry.category.color.toComposeColor()
        )
    }

    Text(
        modifier = Modifier.padding(start = 24.dp),
        text = title,
        style = UI.typo.b1.style(
            color = titleColor
        )
    )

    Spacer(Modifier.height(16.dp))

    var tapEvent: TapEvent? by remember {
        mutableStateOf(null)
    }

    IvyLineChart(
        modifier = Modifier.padding(horizontal = 24.dp),
        title = "",
        functions = functions,
        xLabel = {
            val range =
                categoryValues.first().values.getOrNull(it.toInt())?.range ?: return@IvyLineChart ""
            period.xLabel(range.toCloseTimeRangeUnsafe())
        },
        yLabel = {
            it.format(baseCurrencyCode)
        },
        onTap = {
            tapEvent = it
        }
    )

    tapEvent?.let {
        val value = functions.getOrNull(it.functionIndex)?.values?.get(it.valueIndex)
            ?: return@let

        Spacer(Modifier.height(16.dp))

//        ChartInfoCard(
//            baseCurrencyCode = baseCurrencyCode,
//            backgroundColor = functions[it.functionIndex].color,
//            chartPoint = TimeValue(
//                range = categoryValues[it.functionIndex].values[it.valueIndex].range,
//                period = period,
//                value = value.y
//            ),
//            formatValueAsCount = countChart
//        )

    }
}
