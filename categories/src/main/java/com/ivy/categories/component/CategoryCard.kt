package com.ivy.categories.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.util.ComponentPreview

@Composable
internal fun CategoryCard(
    category: CategoryUi,
    balance: ValueUi,
    onClick: () -> Unit,
) {
    val dynamicContrast = rememberDynamicContrast(category.color)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(UI.shapes.rounded)
            .border(1.dp, dynamicContrast, UI.shapes.rounded)
            .clickable(onClick = onClick)
    ) {
        val contrast = rememberContrast(category.color)
        Header(
            icon = category.icon,
            name = category.name,
            color = category.color,
            contrast = contrast,
        )
        Balance(balance = balance)
    }
}

@Composable
private fun Header(
    icon: ItemIcon,
    name: String,
    color: Color,
    contrast: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, UI.shapes.roundedTop)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemIcon(itemIcon = icon, size = IconSize.M, tint = contrast)
        B2(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            text = name,
            color = contrast,
        )
    }
}

@Composable
private fun Balance(
    balance: ValueUi
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AmountCurrency(value = balance)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        CategoryCard(
            category = dummyCategoryUi("Category"),
            balance = dummyValueUi("-185.00"),
            onClick = {},
        )
    }
}
// endregion