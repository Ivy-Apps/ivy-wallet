package com.ivy.categories.component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.categories.R
import com.ivy.categories.data.CategoryListItemUi.CategoryCard
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.value.AmountCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.isInPreview


@Composable
fun CategoryFolderCard(
    parentCategory: CategoryUi,
    balance: ValueUi,
    categories: List<CategoryCard>,
    categoriesCount: Int,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryUi) -> Unit,
    onParentCategoryClick: () -> Unit,
) {
    val dynamicContrast = rememberDynamicContrast(parentCategory.color)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(UI.shapes.rounded)
            .border(1.dp, dynamicContrast, UI.shapes.rounded)
            .clickable(onClick = onParentCategoryClick),
    ) {
        val contrastColor = rememberContrast(parentCategory.color)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(parentCategory.color, UI.shapes.roundedTop)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 12.dp)
        ) {
            IconNameRow(
                folderName = parentCategory.name,
                folderIcon = parentCategory.icon,
                color = contrastColor
            )
            SpacerVer(height = 2.dp)
            Balance(balance = balance, color = contrastColor)
        }
        var expanded by if (isInPreview()) remember {
            mutableStateOf(previewExpanded)
        } else remember { mutableStateOf(false) }
        ExpandCollapse(
            expanded = expanded,
            color = UI.colorsInverted.pure,
            count = categoriesCount,
            onSetExpanded = { expanded = it }
        )
        Categories(expanded = expanded, items = categories, onClick = onCategoryClick)
    }
}

@Composable
private fun IconNameRow(
    folderName: String,
    folderIcon: ItemIcon,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIcon(
            itemIcon = folderIcon,
            size = IconSize.M,
            tint = color,
        )
        SpacerHor(width = 8.dp)
        B2(
            modifier = Modifier.weight(1f),
            text = folderName,
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun Balance(
    balance: ValueUi,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmountCurrency(value = balance, color = color)
    }
}

@Composable
private fun ExpandCollapse(
    expanded: Boolean,
    color: Color,
    count: Int,
    onSetExpanded: (Boolean) -> Unit
) {
    if (count > 0) {
        IvyButton(
            size = ButtonSize.Big,
            shape = UI.shapes.roundedBottom,
            visibility = Visibility.Low,
            feeling = Feeling.Custom(color),
            text = if (expanded)
                "Tap to collapse ($count)" else "Tap to expand ($count)",
            icon = if (expanded)
                R.drawable.ic_round_expand_less_24 else R.drawable.round_expand_more_24
        ) {
            onSetExpanded(!expanded)
        }
    } else {
        B2(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            text = "Empty folder",
            fontWeight = FontWeight.ExtraBold,
            color = UI.colors.neutral,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
private fun Categories(
    expanded: Boolean,
    items: List<CategoryCard>,
    onClick: (CategoryUi) -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column(Modifier.fillMaxWidth()) {
            items.forEach {
                key(it.category.id) {
                    CategoryCard(
                        category = it.category,
                        balance = it.balance,
                        onClick = { onClick(it.category) }
                    )
                    SpacerVer(height = 8.dp)
                }
            }
            SpacerVer(height = 4.dp)
        }
    }
}


// region Preview
private var previewExpanded = false

@Preview
@Composable
private fun Preview_Collapsed() {
    ComponentPreview {
        CategoryFolderCard(
            parentCategory = dummyCategoryUi("Business"),
            balance = dummyValueUi("5,320.50"),
            categories = emptyList(),
            categoriesCount = 0,
            onCategoryClick = {},
            onParentCategoryClick = {},
        )
    }
}

@Preview
@Composable
private fun Preview_Expanded() {
    ComponentPreview {
        previewExpanded = true
        CategoryFolderCard(
            parentCategory = dummyCategoryUi("Business"),
            balance = dummyValueUi("+3,320.50"),
            categories = listOf(
                CategoryCard(
                    category = dummyCategoryUi("Category 1"),
                    balance = dummyValueUi("-1,000.00"),
                ),
                CategoryCard(
                    category = dummyCategoryUi("Category 2", color = Blue),
                    balance = dummyValueUi("0.00"),
                ),
                CategoryCard(
                    category = dummyCategoryUi("Category 3", color = Red),
                    balance = dummyValueUi("+4,320.50"),
                ),
            ),
            categoriesCount = 3,
            onCategoryClick = {},
            onParentCategoryClick = {},
        )
    }
}
// endregion