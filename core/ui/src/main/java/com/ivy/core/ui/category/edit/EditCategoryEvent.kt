package com.ivy.core.ui.category.edit

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.ItemIconId
import com.ivy.data.category.CategoryType

internal sealed interface EditCategoryEvent {
    data class Initial(val categoryId: String) : EditCategoryEvent

    object EditCategory : EditCategoryEvent

    data class IconChange(val iconId: ItemIconId) : EditCategoryEvent

    data class NameChange(val name: String) : EditCategoryEvent

    data class ColorChange(val color: Color) : EditCategoryEvent

    data class ParentChange(val parent: CategoryUi?) : EditCategoryEvent

    data class TypeChange(val type: CategoryType) : EditCategoryEvent

    object Archive : EditCategoryEvent
    object Unarchive : EditCategoryEvent
    object Delete : EditCategoryEvent
}