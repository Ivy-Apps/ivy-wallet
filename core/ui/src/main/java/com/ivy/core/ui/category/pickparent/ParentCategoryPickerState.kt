package com.ivy.core.ui.category.pickparent

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.CategoryUi

@Immutable
internal data class ParentCategoryPickerState(
    val categories: List<CategoryUi>
)