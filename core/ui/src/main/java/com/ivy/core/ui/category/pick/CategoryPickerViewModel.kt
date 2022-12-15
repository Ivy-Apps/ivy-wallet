package com.ivy.core.ui.category.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class CategoryPickerViewModel @Inject constructor(
    categoriesFlow: CategoriesFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
) : SimpleFlowViewModel<CategoryPickerState, Unit>() {
    override val initialUi = CategoryPickerState(categories = emptyList())

    override val uiFlow: Flow<CategoryPickerState> =
        categoriesFlow().map { categories ->
            CategoryPickerState(
                categories = categories
                    .filter { it.parentCategoryId == null }
                    .map { mapCategoryUiAct(it) }
            )
        }

    // region Event Handling
    override suspend fun handleEvent(event: Unit) {}
    // endregion
}