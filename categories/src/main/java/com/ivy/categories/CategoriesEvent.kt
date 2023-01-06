package com.ivy.categories

import com.ivy.core.ui.data.CategoryUi

sealed interface CategoriesEvent {
    data class CategoryClick(val category: CategoryUi) : CategoriesEvent
}