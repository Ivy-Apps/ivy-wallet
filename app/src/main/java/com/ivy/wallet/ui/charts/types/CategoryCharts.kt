package com.ivy.wallet.ui.charts.types

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.format
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.charts.Period
import com.ivy.wallet.ui.charts.TimeValue
import com.ivy.wallet.ui.charts.toValues
import com.ivy.wallet.ui.reports.ListItem
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.asBrush
import com.ivy.wallet.ui.theme.components.charts.Function
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.toComposeColor

fun LazyListScope.categoryCharts(
    period: Period,
    baseCurrencyCode: String,
    categories: List<Category>,
    categoryValues: Map<Category, List<TimeValue>>,

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
                        categoryValues.containsKey(category)
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
        Spacer(Modifier.height(32.dp))

        val functions = categoryValues.map { entry ->
            Function(
                values = entry.value.toValues(),
                color = { _, _ -> entry.key.color.toComposeColor().asBrush() }
            )
        }

        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = "Category expenses chart",
            style = Typo.body1
        )

        Spacer(Modifier.height(16.dp))

        IvyLineChart(
            modifier = Modifier.padding(horizontal = 24.dp),
            functions = functions,
            xLabel = {
                categoryValues.values.first()[it.toInt()].dateTime.month.name.first().uppercase()
            },
            yLabel = {
                it.format(baseCurrencyCode)
            },
            onTap = {
                //TODO: Implement
            }
        )
    }
}