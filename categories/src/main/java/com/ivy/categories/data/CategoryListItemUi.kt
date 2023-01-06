package com.ivy.categories.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi

@Immutable
sealed interface CategoryListItemUi {
    @Immutable
    data class CategoryCard(
        val category: CategoryUi,
        val balance: ValueUi,
    ) : CategoryListItemUi

    @Immutable
    data class ParentCategory(
        val parentCategory: CategoryUi,
        val balance: ValueUi,
        val categoryCards: List<CategoryCard>,
        val categoriesCount: Int,
    ) : CategoryListItemUi

    @Immutable
    data class Archived(
        val categoryCards: List<CategoryCard>,
        val count: Int,
    ) : CategoryListItemUi
}