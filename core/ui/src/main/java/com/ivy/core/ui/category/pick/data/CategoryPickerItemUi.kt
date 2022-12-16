package com.ivy.core.ui.category.pick.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.account.pick.data.SelectableAccountUi

@Immutable
sealed interface CategoryPickerItemUi {
    data class CategoriesRow(
        val categories: List<SelectableCategoryUi>
    ) : CategoryPickerItemUi

    data class ParentCategory(
        val parent: SelectableCategoryUi,
        val expanded: Boolean,
        val children: List<SelectableAccountUi>
    ) : CategoryPickerItemUi
}