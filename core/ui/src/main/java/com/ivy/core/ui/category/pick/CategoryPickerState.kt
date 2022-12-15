package com.ivy.core.ui.category.pick

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.CategoryUi

@Immutable
internal data class CategoryPickerState(
    val categories: List<CategoryUi>
)