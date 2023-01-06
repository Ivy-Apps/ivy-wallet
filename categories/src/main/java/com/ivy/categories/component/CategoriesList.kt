package com.ivy.categories.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.categories.R
import com.ivy.categories.data.CategoryListItemUi
import com.ivy.categories.data.CategoryListItemUi.*
import com.ivy.core.ui.data.CategoryUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

fun LazyListScope.categoriesList(
    items: List<CategoryListItemUi>,
    emptyState: Boolean,
    onCategoryClick: (CategoryUi) -> Unit,
    onParentCategoryClick: (CategoryUi) -> Unit,
    onCreateCategory: () -> Unit,
) {
    items(
        items = items,
        key = {
            when (it) {
                is Archived -> "archived"
                is CategoryCard -> it.category.id
                is ParentCategory -> it.parentCategory.id
            }
        }
    ) { item ->
        when (item) {
            is CategoryCard -> {
                SpacerVer(height = 8.dp)
                CategoryCard(
                    category = item.category,
                    balance = item.balance,
                    onClick = { onCategoryClick(item.category) }
                )
            }
            is ParentCategory -> {
                SpacerVer(height = 8.dp)
                CategoryFolderCard(
                    parentCategory = item.parentCategory,
                    balance = item.balance,
                    categories = item.categoryCards,
                    categoriesCount = item.categoriesCount,
                    onCategoryClick = onCategoryClick,
                    onParentCategoryClick = {
                        onParentCategoryClick(item.parentCategory)
                    },
                )
            }
            is Archived -> {
                SpacerVer(height = 16.dp)
                ArchivedCategories(archived = item, onCategoryClick = onCategoryClick)
            }
        }
    }

    if (emptyState) {
        item {
            EmptyState(onCreateCategory = onCreateCategory)
        }
    }
}

@Composable
private fun EmptyState(
    onCreateCategory: () -> Unit
) {
    SpacerVer(height = 96.dp)
    B1(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "No Categories",
        textAlign = TextAlign.Center,
        color = UI.colors.primary
    )
    SpacerVer(height = 12.dp)
    B2(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "Create categories to better organize you transactions",
        textAlign = TextAlign.Center
    )
    SpacerVer(height = 12.dp)
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Focused,
        feeling = Feeling.Positive,
        text = "Create Category",
        icon = R.drawable.ic_custom_category_s,
        onClick = onCreateCategory,
    )
    SpacerVer(height = 24.dp)
}


// region Preview
@Preview
@Composable
private fun Preview_EmptyState() {
    ComponentPreview {
        LazyColumn {
            categoriesList(
                items = emptyList(),
                emptyState = false,
                onCategoryClick = {},
                onCreateCategory = {},
                onParentCategoryClick = {},
            )
        }
    }
}
// endregion