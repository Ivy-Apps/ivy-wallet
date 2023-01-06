package com.ivy.core.ui.category.pickparent

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ParentCategoryPickerViewModel @Inject constructor(
    categoriesFlow: CategoriesFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
) : SimpleFlowViewModel<ParentCategoryPickerState, Unit>() {
    override val initialUi = ParentCategoryPickerState(categories = emptyList())

    override val uiFlow: Flow<ParentCategoryPickerState> =
        categoriesFlow().map { categories ->
            ParentCategoryPickerState(
                categories = categories
                    .filter { it.parentCategoryId == null }
                    .map { mapCategoryUiAct(it) }
            )
        }

    // region Event Handling
    override suspend fun handleEvent(event: Unit) {}
    // endregion
}