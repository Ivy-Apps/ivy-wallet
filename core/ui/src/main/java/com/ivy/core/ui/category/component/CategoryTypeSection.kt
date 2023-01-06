package com.ivy.core.ui.category.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.data.category.CategoryType
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview


@Composable
fun CategoryTypeSection(
    type: CategoryType,
    onSelect: (CategoryType) -> Unit
) {
    B1(
        modifier = Modifier.padding(start = 24.dp),
        text = "Category type"
    )
    SpacerVer(height = 8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryTypeButton(
            modifier = Modifier.weight(1f),
            type = CategoryType.Income,
            selected = type == CategoryType.Income,
            onSelect = onSelect
        )
        SpacerHor(width = 8.dp)
        CategoryTypeButton(
            modifier = Modifier.weight(1f),
            type = CategoryType.Expense,
            selected = type == CategoryType.Expense,
            onSelect = onSelect
        )
        SpacerHor(width = 8.dp)
        CategoryTypeButton(
            modifier = Modifier.weight(1f),
            type = CategoryType.Both,
            selected = type == CategoryType.Both,
            onSelect = onSelect
        )
    }
}

@Composable
private fun CategoryTypeButton(
    type: CategoryType,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: (CategoryType) -> Unit
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = if (selected) Visibility.High else Visibility.Medium,
        feeling = if (selected) Feeling.Custom(
            when (type) {
                CategoryType.Income -> UI.colors.green
                CategoryType.Expense -> UI.colors.red
                CategoryType.Both -> UI.colors.primary
            }
        ) else Feeling.Neutral,
        text = when (type) {
            CategoryType.Expense -> "Expense"
            CategoryType.Income -> "Income"
            CategoryType.Both -> "Both"
        },
        icon = null
    ) {
        onSelect(type)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Column {
            CategoryTypeSection(
                type = CategoryType.Both,
                onSelect = {}
            )
        }
    }
}
// endregion