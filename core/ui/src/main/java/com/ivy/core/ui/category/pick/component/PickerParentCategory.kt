package com.ivy.core.ui.category.pick.component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.category.pick.data.CategoryPickerItemUi
import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.category.pick.data.dummySelectableCategoryUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenWhen

@Composable
internal fun PickerParentCategory(
    item: CategoryPickerItemUi.ParentCategory,
    onParentClick: () -> Unit,
    onChildClick: (CategoryUi) -> Unit
) {
    ParentCategoryItem(parent = item.parent, onClick = onParentClick)
    AnimatedVisibility(
        visible = item.expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            PickerCategoriesRow(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp),
                categories = item.children,
                onSelect = { onChildClick(it.category) },
            )
            DividerHor()
        }
    }
}

@Composable
private fun ParentCategoryItem(
    parent: SelectableCategoryUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(UI.shapes.rounded)
            .thenWhen {
                when (parent.selected) {
                    true -> background(parent.category.color, UI.shapes.rounded)
                    false -> border(1.dp, parent.category.color, UI.shapes.rounded)
                }
            }
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 16.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contrast = if (parent.selected)
            rememberContrast(parent.category.color) else UI.colorsInverted.pure
        ItemIcon(
            itemIcon = parent.category.icon,
            size = IconSize.S,
            tint = contrast
        )
        SpacerHor(width = 8.dp)
        B2(text = parent.category.name, color = contrast)
    }
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            PickerParentCategory(
                item = CategoryPickerItemUi.ParentCategory(
                    parent = dummySelectableCategoryUi(),
                    children = listOf(
                        dummySelectableCategoryUi(),
                        dummySelectableCategoryUi(),
                        dummySelectableCategoryUi(),
                    ),
                    expanded = true
                ),
                onParentClick = {},
                onChildClick = {}
            )
        }
    }
}