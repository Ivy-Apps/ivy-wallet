package com.ivy.categories

import com.ivy.core.domain.SimpleFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(

) : SimpleFlowViewModel<CategoryState, CategoryEvent>() {
    override val initialUi = CategoryState(
        selectedPeriod = null,
        items = listOf(),
        emptyState = true,
    )

    override val uiFlow: Flow<CategoryState>
        get() = TODO("Not yet implemented")


    // region Event handling
    override suspend fun handleEvent(event: CategoryEvent) = when (event) {
        is CategoryEvent.CategoryClick -> handleCategoryClick(event)
    }

    private fun handleCategoryClick(event: CategoryEvent.CategoryClick) {
        // TODO: Implement
    }
    // endregion
}