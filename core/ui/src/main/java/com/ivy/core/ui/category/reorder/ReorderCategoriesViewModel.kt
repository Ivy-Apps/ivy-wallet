package com.ivy.core.ui.category.reorder

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.category.reorder.ReorderCategoriesViewModel.InternalState
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ReorderCategoriesViewModel @Inject constructor(
    categoriesFlow: CategoriesFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val timeProvider: TimeProvider,
) : FlowViewModel<InternalState, ReorderCategoriesStateUi, ReorderCategoriesEvent>() {
    override val initialState = InternalState(
        categories = emptyList(),
    )

    override val initialUi = ReorderCategoriesStateUi(
        items = emptyList(),
    )

    override val stateFlow: Flow<InternalState> = categoriesFlow().map { categories ->
        InternalState(
            categories = categories,
        )
    }

    override val uiFlow: Flow<ReorderCategoriesStateUi> = stateFlow
        .map { internalState ->
            ReorderCategoriesStateUi(
                items = internalState.categories.map { mapCategoryUiAct(it) },
            )
        }


    // region Event handling
    override suspend fun handleEvent(event: ReorderCategoriesEvent) = when (event) {
        is ReorderCategoriesEvent.Reorder -> handleReorder(event)
    }

    private suspend fun handleReorder(event: ReorderCategoriesEvent.Reorder) {
        val categoriesMap = state.value.categories.associateBy { it.id.toString() }

        val reordered = event.reordered.mapIndexedNotNull { index, item ->
            categoriesMap[item.id]
                ?.copy(
                    orderNum = index.toDouble(),
                    sync = Sync(
                        state = SyncState.Syncing,
                        lastUpdated = timeProvider.timeNow(),
                    )
                )
        }

        val expectedCount = uiState.value.items.size
        // verify no lost of data
        if (reordered.size == expectedCount) {
            writeCategoriesAct(Modify.saveMany(reordered))
        }
    }

    // endregion

    data class InternalState(
        val categories: List<Category>,
    )
}