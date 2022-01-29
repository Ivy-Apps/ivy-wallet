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
import com.ivy.wallet.R
import com.ivy.wallet.base.format
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.charts.CategoryValues
import com.ivy.wallet.ui.charts.ChartPeriod
import com.ivy.wallet.ui.charts.TimeValue
import com.ivy.wallet.ui.charts.toValues
import com.ivy.wallet.ui.reports.ListItem
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.charts.Function
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.TapEvent

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
            title = "Expenses",
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryExpenseValues
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = "Expenses count",
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryExpenseCount
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = "Income",
            titleColor = Green,
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryIncomeValues
        )
    }

    item {
        CategoriesChart(
            period = period,
            title = "Income count",
            titleColor = Green,
            baseCurrencyCode = baseCurrencyCode,
            categoryValues = categoryIncomeCount
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
    titleColor: Color = IvyTheme.colors.pureInverse,
    baseCurrencyCode: String,
    categoryValues: List<CategoryValues>
) {
    Spacer(Modifier.height(48.dp))

    val functions = categoryValues.map { entry ->
        Function(
            values = entry.values.toValues(),
            color = entry.category.color.toComposeColor()
        )
    }

    Text(
        modifier = Modifier.padding(start = 24.dp),
        text = title,
        style = Typo.body1.style(
            color = titleColor
        )
    )

    Spacer(Modifier.height(16.dp))

    var tapEvent: TapEvent? by remember {
        mutableStateOf(null)
    }

    IvyLineChart(
        modifier = Modifier.padding(horizontal = 24.dp),
        functions = functions,
        xLabel = {
            val range =
                categoryValues.first().values.getOrNull(it.toInt())?.range ?: return@IvyLineChart ""
            period.xLabel(range)
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

        ChartInfoCard(
            baseCurrencyCode = baseCurrencyCode,
            backgroundColor = functions[it.functionIndex].color,
            timeValue = TimeValue(
                range = categoryValues[it.functionIndex].values[it.valueIndex].range,
                period = period,
                value = value.y
            )
        )

    }
}