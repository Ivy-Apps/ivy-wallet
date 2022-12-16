package com.ivy.core.ui.category.pick.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi

@Immutable
data class SelectableCategoryUi(
    val category: CategoryUi,
    val selected: Boolean
)

fun dummySelectableCategoryUi(
    category: CategoryUi = dummyCategoryUi(),
    selected: Boolean = false
) = SelectableCategoryUi(
    category = category,
    selected = selected
)