package com.ivy.core.ui.category.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Composable
internal fun ColumnScope.ParentCategoryButton(
    parent: CategoryUi?,
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit
) {
    B1(
        modifier = Modifier.padding(start = 24.dp),
        text = "Parent category"
    )
    SpacerVer(height = 8.dp)
    if (parent != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(UI.shapes.rounded)
                .background(parent.color)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val contrast = rememberContrast(color = parent.color)
            ItemIcon(itemIcon = parent.icon, size = IconSize.S, tint = contrast)
            SpacerHor(width = 12.dp)
            B1(text = parent.name, color = contrast)
        }
    } else {
        IvyButton(
            modifier = modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Custom(color),
            text = "Choose parent",
            icon = R.drawable.ic_custom_category_s,
            onClick = onClick
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview_None() {
    ComponentPreview {
        Column {
            ParentCategoryButton(
                parent = null,
                color = Purple,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Selected() {
    ComponentPreview {
        Column {
            ParentCategoryButton(
                parent = dummyCategoryUi("Parent"),
                color = Purple,
                onClick = {}
            )
        }
    }
}
// endregion