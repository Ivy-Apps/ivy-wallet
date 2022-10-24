package com.ivy.categories

import com.ivy.core.ui.data.CategoryUi

sealed interface CategoryEvent {
    data class CategoryClick(val category: CategoryUi) : CategoryEvent
}