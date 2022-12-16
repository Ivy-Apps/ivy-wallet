package com.ivy.core.ui.category.pick

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.category.pick.data.CategoryPickerItemUi

@Immutable
data class CategoryPickerState(
    val items: List<CategoryPickerItemUi>
)