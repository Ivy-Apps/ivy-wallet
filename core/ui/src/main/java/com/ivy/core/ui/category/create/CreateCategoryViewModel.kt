package com.ivy.core.ui.category.create

import androidx.compose.ui.graphics.toArgb
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.category.NewCategoryOrderNumAct
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
internal class CreateCategoryViewModel @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val newCategoryOrderNumAct: NewCategoryOrderNumAct,
    private val timeProvider: TimeProvider,
) : SimpleFlowViewModel<CreateCategoryState, CreateCategoryEvent>() {
    override val initialUi = CreateCategoryState(
        icon = ItemIcon.Sized(
            iconS = R.drawable.ic_custom_category_s,
            iconM = R.drawable.ic_custom_category_m,
            iconL = R.drawable.ic_custom_category_l,
            iconId = null
        )
    )

    private var name = ""
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val categoryType = MutableStateFlow(CategoryType.Both)

    override val uiFlow: Flow<CreateCategoryState> = iconId.map { iconId ->
        CreateCategoryState(
            icon = itemIconAct(ItemIconAct.Input(iconId, DefaultTo.Account))
        )
    }

    // region Event Handling
    override suspend fun handleEvent(event: CreateCategoryEvent) = when (event) {
        is CreateCategoryEvent.CreateCategory -> createCategory(event)
        is CreateCategoryEvent.IconChange -> handleIconPick(event)
        is CreateCategoryEvent.NameChange -> handleNameChange(event)
        is CreateCategoryEvent.CategoryTypeChange -> handleCategoryTypeChange(event)
    }

    private suspend fun createCategory(event: CreateCategoryEvent.CreateCategory) {
        val new = Category(
            id = UUID.randomUUID(),
            name = name,
            color = event.color.toArgb(),
            icon = iconId.value,
            parentCategoryId = event.parent?.id?.toUUID(),
            orderNum = newCategoryOrderNumAct(Unit),
            state = CategoryState.Default,
            type = categoryType.value,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            ),
        )
        writeCategoriesAct(Modify.save(new))
    }

    private fun handleIconPick(event: CreateCategoryEvent.IconChange) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: CreateCategoryEvent.NameChange) {
        name = event.name
    }

    private fun handleCategoryTypeChange(event: CreateCategoryEvent.CategoryTypeChange) {
        categoryType.value = event.categoryType
    }
    // endregion
}