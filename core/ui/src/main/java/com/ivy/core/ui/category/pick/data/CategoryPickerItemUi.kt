package com.ivy.core.ui.category.pick.data

import androidx.compose.runtime.Immutable

@Immutable
sealed interface CategoryPickerItemUi {
    data class CategoriesRow(
        val categories: List<SelectableCategoryUi>
    ) : CategoryPickerItemUi

    data class ParentCategory(
        val parent: SelectableCategoryUi,
        val expanded: Boolean,
        val children: List<SelectableCategoryUi>
    ) : CategoryPickerItemUi
}