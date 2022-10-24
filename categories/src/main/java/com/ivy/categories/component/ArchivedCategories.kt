package com.ivy.categories.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.categories.R
import com.ivy.categories.data.CategoryListItemUi
import com.ivy.categories.data.CategoryListItemUi.CategoryCard
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.design.l0_system.color.Blue
import com.ivy.design.l0_system.color.Red
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.isInPreview

@Composable
internal fun ArchivedCategories(
    archived: CategoryListItemUi.Archived,
    onCategoryClick: (CategoryUi) -> Unit,
) {
    var expanded by if (isInPreview()) remember {
        mutableStateOf(previewExpanded)
    } else remember { mutableStateOf(false) }
    ArchivedDivider(
        expanded = expanded,
        count = archived.count,
        onSetExpanded = { expanded = it }
    )
    AccountsList(
        categories = archived.categoryCards,
        expanded = expanded,
        onCategoryClick = onCategoryClick
    )
}

@Composable
private fun ArchivedDivider(
    expanded: Boolean,
    count: Int,
    onSetExpanded: (Boolean) -> Unit
) {
    IvyButton(
        size = ButtonSize.Big,
        visibility = Visibility.Low,
        feeling = Feeling.Neutral,
        text = "Archived ($count)",
        icon = if (expanded)
            R.drawable.round_expand_more_24 else R.drawable.ic_round_expand_less_24
    ) {
        onSetExpanded(!expanded)
    }
}

@Composable
private fun AccountsList(
    categories: List<CategoryCard>,
    expanded: Boolean,
    onCategoryClick: (CategoryUi) -> Unit,
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            categories.forEach { item ->
                key("archived_${item.category.id}") {
                    SpacerVer(height = 12.dp)
                    CategoryCard(
                        category = item.category,
                        balance = item.balance,
                    ) {
                        onCategoryClick(item.category)
                    }
                }
            }
        }
    }
}


// region Preview
private var previewExpanded = false

@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        previewExpanded = true
        Column {
            ArchivedCategories(
                archived = CategoryListItemUi.Archived(
                    categoryCards = listOf(
                        CategoryCard(
                            category = dummyCategoryUi("Category 1"),
                            balance = dummyValueUi("-1,000.00", "BGN"),
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
                    count = 3,
                ),
                onCategoryClick = {}
            )
        }
    }
}
// endregion