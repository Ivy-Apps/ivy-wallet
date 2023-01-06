package com.ivy.core.ui.category.pick.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.category.pick.data.dummySelectableCategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.WrapContentRow
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenWhen

@Composable
internal fun PickerCategoriesRow(
    categories: List<SelectableCategoryUi>,
    modifier: Modifier = Modifier,
    onSelect: (SelectableCategoryUi) -> Unit,
) {
    WrapContentRow(
        modifier = modifier.padding(horizontal = 8.dp),
        items = categories,
        itemKey = { it.category.id },
        horizontalMarginBetweenItems = 8.dp,
        verticalMarginBetweenRows = 8.dp,
    ) { item ->
        CategoryItem(
            item = item,
            onClick = { onSelect(item) },
        )
    }
}

@Composable
private fun CategoryItem(
    item: SelectableCategoryUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val category = item.category
    Row(
        modifier = modifier
            .clip(UI.shapes.rounded)
            .thenWhen {
                when (item.selected) {
                    true -> background(category.color, UI.shapes.rounded)
                    false -> border(1.dp, category.color, UI.shapes.rounded)
                }
            }
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 16.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contrast = if (item.selected)
            rememberContrast(category.color) else UI.colorsInverted.pure
        ItemIcon(
            itemIcon = category.icon,
            size = IconSize.S,
            tint = contrast
        )
        SpacerHor(width = 4.dp)
        B2(text = category.name, color = contrast)
    }
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        PickerCategoriesRow(
            categories = listOf(
                dummySelectableCategoryUi(
                    category = dummyCategoryUi(
                        name = "Car",
                        icon = dummyIconUnknown(R.drawable.ic_vue_transport_car)
                    )
                ),
                dummySelectableCategoryUi(selected = true),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi()
            ),
            onSelect = {}
        )
    }
}