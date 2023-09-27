package com.ivy.piechart

import androidx.compose.runtime.Immutable
import com.ivy.legacy.datamodel.Category

@Immutable
data class SelectedCategory(
    val category: Category? // null - Unspecified
)
