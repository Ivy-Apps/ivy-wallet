package com.ivy.core.ui.category.reorder

import com.ivy.core.ui.data.CategoryUi

sealed interface ReorderCategoriesEvent {
    data class Reorder(
        val reordered: List<CategoryUi>
    ) : ReorderCategoriesEvent
}