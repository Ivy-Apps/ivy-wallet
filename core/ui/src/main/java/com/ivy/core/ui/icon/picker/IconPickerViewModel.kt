package com.ivy.core.ui.icon.picker

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.ui.action.ItemIconOptionalAct
import com.ivy.core.ui.icon.picker.data.PickerItemUi
import com.ivy.core.ui.icon.picker.data.PickerItemUnverified
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class IconPickerViewModel @Inject constructor(
    private val itemIconOptionalAct: ItemIconOptionalAct
) : FlowViewModel<IconPickerStateUi, IconPickerStateUi, IconPickerEvent>() {
    override fun initialState(): IconPickerStateUi = IconPickerStateUi(
        items = emptyList(),
        searchQuery = ""
    )

    override fun initialUiState(): IconPickerStateUi = initialState()

    private val searchQuery = MutableStateFlow("")

    override fun stateFlow(): Flow<IconPickerStateUi> = iconsFlow().map { icons ->
        IconPickerStateUi(
            items = icons.mapNotNull {
                when (it) {
                    is PickerItemUnverified.SectionDivider ->
                        PickerItemUi.Section(it.name)
                    is PickerItemUnverified.Icon ->
                        itemIconOptionalAct(it.iconId)?.let(PickerItemUi::Icon)
                }
            },
            searchQuery = searchQuery.value
        )
    }

    @OptIn(FlowPreview::class)
    private fun iconsFlow() = searchQuery
        .debounce(300)
        .map { query ->
            val pickerItems = pickerItems()
            val normalizedQuery = query.trim().lowercase().takeIf { it.isNotEmpty() }
            if (normalizedQuery != null) {
                pickerItems
                    .filterIsInstance<PickerItemUnverified.Icon>() // remove section dividers
                    .filter {
                        it.keywords.any { keyword -> normalizedQuery.contains(keyword) }
                    }
            } else pickerItems
        }

    override suspend fun mapToUiState(state: IconPickerStateUi): IconPickerStateUi = state

    // region Event Handling
    override suspend fun handleEvent(event: IconPickerEvent) = when (event) {
        is IconPickerEvent.Search -> handleSearchQuery(event)
    }

    private fun handleSearchQuery(event: IconPickerEvent.Search) {
        searchQuery.value = event.query.lowercase()
    }
    // endregion
}