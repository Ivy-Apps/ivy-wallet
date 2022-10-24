package com.ivy.core.domain.action.data

import androidx.compose.runtime.Immutable
import com.ivy.data.category.Category

@Immutable
sealed interface CategoryListItem {
    @Immutable
    data class CategoryHolder(
        val category: Category,
    ) : CategoryListItem

    @Immutable
    data class ParentCategory(
        val parentCategory: Category,
        val categoryCards: List<Category>,
    ) : CategoryListItem

    @Immutable
    data class Archived(
        val categoryCards: List<CategoryHolder>,
    ) : CategoryListItem
}