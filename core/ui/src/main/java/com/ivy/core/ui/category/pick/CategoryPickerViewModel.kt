package com.ivy.core.ui.category.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.ui.category.pick.action.CategoryPickerItemsFlow
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.transaction.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CategoryPickerViewModel @Inject constructor(
    private val categoryPickerItemsFlow: CategoryPickerItemsFlow
) : SimpleFlowViewModel<CategoryPickerState, CategoryPickerEvent>() {
    override val initialUi = CategoryPickerState(
        items = emptyList()
    )

    private val trnType = MutableStateFlow<TransactionType?>(null)
    private val expandedParent = MutableStateFlow<CategoryUi?>(null)
    private val selectedCategory = MutableStateFlow<CategoryUi?>(null)

    override val uiFlow: Flow<CategoryPickerState> = combine(
        selectedCategory, expandedParent, trnType
    ) { selectedCategory, expandedParent, trnType ->
        categoryPickerItemsFlow(
            CategoryPickerItemsFlow.Input(
                selectedCategory = selectedCategory,
                expandedParent = expandedParent,
                trnType = trnType,
            )
        ).map {
            CategoryPickerState(items = it)
        }
    }.flattenLatest()


    // region Event Handling
    override suspend fun handleEvent(event: CategoryPickerEvent) = when (event) {
        is CategoryPickerEvent.Initial -> handleInitial(event)
        is CategoryPickerEvent.CategorySelected -> handleCategorySelected(event)
        is CategoryPickerEvent.ExpandParent -> handleExpandParent(event)
        CategoryPickerEvent.CollapseParent -> handleCollapseParent()
    }

    private fun handleInitial(event: CategoryPickerEvent.Initial) {
        trnType.value = event.trnType
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