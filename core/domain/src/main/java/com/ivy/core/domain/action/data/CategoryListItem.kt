package com.ivy.core.domain.action.data

import com.ivy.data.category.Category

sealed interface CategoryListItem {
    data class CategoryHolder(
        val category: Category,
    ) : CategoryListItem

    data class ParentCategory(
        val parentCategory: Category,
        val categories: List<Category>,
    ) : CategoryListItem

    data class Archived(
        val categories: List<Category>,
    ) : CategoryListItem
}