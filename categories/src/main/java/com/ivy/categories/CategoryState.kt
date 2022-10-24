package com.ivy.categories

import androidx.compose.runtime.Immutable
import com.ivy.categories.data.CategoryListItemUi

@Immutable
data class CategoryState(
    val items: List<CategoryListItemUi>,
)