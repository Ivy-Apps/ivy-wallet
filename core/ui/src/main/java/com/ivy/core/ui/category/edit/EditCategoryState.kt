package com.ivy.core.ui.category.edit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.category.CategoryType

@Immutable
internal data class EditCategoryState(
    val categoryId: String,
    val icon: ItemIcon,
    val color: Color,
    val initialName: String,
    val parent: CategoryUi?,
    val archived: Boolean,
    val type: CategoryType,
)