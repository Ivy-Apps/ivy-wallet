package com.ivy.categories

import androidx.compose.runtime.Immutable
import com.ivy.categories.data.CategoryListItemUi
import com.ivy.core.ui.data.period.SelectedPeriodUi

@Immutable
data class CategoriesState(
    val selectedPeriod: SelectedPeriodUi?,
    val items: List<CategoryListItemUi>,
    val emptyState: Boolean,
)