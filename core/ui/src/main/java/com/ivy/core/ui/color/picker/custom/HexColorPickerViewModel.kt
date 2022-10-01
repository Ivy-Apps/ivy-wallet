package com.ivy.core.ui.color.picker.custom

import com.ivy.core.domain.FlowViewModel
import com.ivy.design.l0_system.color.fromHex
import com.ivy.design.l0_system.color.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class HexColorPickerViewModel @Inject constructor(
) : FlowViewModel<HexColorPickerState, HexColorPickerState, HexColorPickerEvent>() {
    override fun initialState() = HexColorPickerState(
        hex = "",
        color = null,
    )

    override fun initialUiState(): HexColorPickerState = initialState()

    private val hexFlow = MutableStateFlow("")

    override fun stateFlow(): Flow<HexColorPickerState> = hexFlow.map { hex ->
        HexColorPickerState(
            hex = "#$hex",
            color = fromHex(hex)
        )
    }

    override suspend fun mapToUiState(state: HexColorPickerState) = state

    // region Event Handling
    override suspend fun handleEvent(event: HexColorPickerEvent) = when (event) {
        is HexColorPickerEvent.Hex -> handleHex(event)
        is HexColorPickerEvent.SetColor -> setColor(event)
    }

    private fun setColor(event: HexColorPickerEvent.SetColor) {
        hexFlow.value = event.color.toHex()
    }

    private fun handleHex(event: HexColorPickerEvent.Hex) {
        hexFlow.value = event.hex.replace("#", "")
    }
    // endregion
}