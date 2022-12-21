package com.ivy.transaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.transaction.R

@Composable
internal fun CategoryComponent(
    category: CategoryUi?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (category != null) {
        CategoryButton(
            modifier = modifier,
            category = category,
            onClick = onClick,
        )
    } else {
        AddCategoryButton(
            modifier = modifier,
            onClick = onClick
        )
    }
}

@Composable
private fun CategoryButton(
    category: CategoryUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.rounded)
            .background(category.color, UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 24.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrast = rememberContrast(category.color)
        ItemIcon(
            itemIcon = category.icon,
            size = IconSize.S,
            tint = contrast
        )
        SpacerHor(width = 12.dp)
        B2(text = category.name, color = contrast)
    }
}

@Composable
private fun AddCategoryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = stringResource(R.string.add_category),
        icon = R.drawable.ic_round_add_24,
        onClick = onClick
    )
}


// region Preview
@Preview
@Composable
private fun Preview_NoCategory() {
    ComponentPreview {
        CategoryComponent(
            category = null,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_WithCategory() {
    ComponentPreview {
        CategoryComponent(
            category = dummyCategoryUi(),
            onClick = {}
        )
    }
}
// endregion