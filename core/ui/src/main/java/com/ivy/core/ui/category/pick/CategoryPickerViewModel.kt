package com.ivy.core.ui.category.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.ui.category.pick.action.CategoryPickerItemsFlow
import com.ivy.core.ui.data.CategoryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoryPickerViewModel @Inject constructor(
    private val categoryPickerItemsFlow: CategoryPickerItemsFlow
) : SimpleFlowViewModel<CategoryPickerState, CategoryPickerEvent>() {
    override val initialUi = CategoryPickerState(
        items = emptyList()
    )

    private val expandedParent = MutableStateFlow<CategoryUi?>(null)
    private val selectedCategory = MutableStateFlow<CategoryUi?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val uiFlow: Flow<CategoryPickerState> = combine(
        selectedCategory, expandedParent
    ) { selectedCategory, expandedParent ->
        categoryPickerItemsFlow(
            CategoryPickerItemsFlow.Input(
                selectedCategory,
                expandedParent
            )
        ).map {
            CategoryPickerState(items = it)
        }
    }.flatMapLatest { it }


    // region Event Handling
    override suspend fun handleEvent(event: CategoryPickerEvent) = when (event) {
        is CategoryPickerEvent.CategorySelected -> handleCategorySelected(event)
        is CategoryPickerEvent.ExpandParent -> handleExpandParent(event)
        CategoryPickerEvent.CollapseParent -> handleCollapseParent()
    }

    private fun handleCategorySelected(event: CategoryPickerEvent.CategorySelected) {
        selectedCategory.value = event.category
    }

    private fun handleExpandParent(event: CategoryPickerEvent.ExpandParent) {
        expandedParent.value = event.parent.category
    }

    private fun handleCollapseParent() {
        expandedParent.value = null
    }
    // endregion
}