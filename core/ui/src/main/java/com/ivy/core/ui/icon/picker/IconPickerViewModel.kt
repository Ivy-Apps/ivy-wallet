package com.ivy.core.ui.icon.picker

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.ui.action.ItemIconOptionalAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class IconPickerViewModel @Inject constructor(
    private val itemIconOptionalAct: ItemIconOptionalAct
) : FlowViewModel<IconPickerStateUi, IconPickerStateUi, Unit>() {
    override fun initialState(): IconPickerStateUi = IconPickerStateUi(emptyList())

    override fun initialUiState(): IconPickerStateUi = initialState()

    override fun stateFlow(): Flow<IconPickerStateUi> = flow {
        val unverifiedIcons = pickerItems()
        IconPickerStateUi(
            items = unverifiedIcons.mapNotNull {
                when (it) {
                    is PickerItemUnverified.SectionDivider ->
                        PickerItemUi.Section(it.name)
                    is PickerItemUnverified.Icon ->
                        itemIconOptionalAct(it.iconId)?.let(PickerItemUi::Icon)
                }
            }
        )
    }

    override suspend fun mapToUiState(state: IconPickerStateUi): IconPickerStateUi = state

    override suspend fun handleEvent(event: Unit) {}
}