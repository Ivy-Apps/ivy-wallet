package com.ivy.core.ui.category.pick

import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.data.CategoryUi

sealed interface CategoryPickerEvent {
    data class CategorySelected(val category: CategoryUi) : CategoryPickerEvent

    data class ParentCategoryClick(val parent: SelectableCategoryUi) : CategoryPickerEvent
}