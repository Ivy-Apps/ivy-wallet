package com.ivy.core.ui.category.create

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.ItemIconId
import com.ivy.data.category.CategoryType

internal sealed interface CreateCategoryEvent {
    data class CreateCategory(
        val color: Color,
        val parent: CategoryUi?
    ) : CreateCategoryEvent

    data class IconChange(val iconId: ItemIconId) : CreateCategoryEvent

    data class NameChange(val name: String) : CreateCategoryEvent

    data class CategoryTypeChange(val categoryType: CategoryType) : CreateCategoryEvent
}