package com.ivy.core.ui.color.picker.custom

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.design.l0_system.color.fromHex
import com.ivy.design.l0_system.color.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class HexColorPickerViewModel @Inject constructor(
) : SimpleFlowViewModel<HexColorPickerState, HexColorPickerEvent>() {
    override val initialUi = HexColorPickerState(
        hex = "",
        color = null,
    )

    private val hexFlow = MutableStateFlow("")

    override val uiFlow: Flow<HexColorPickerState> = hexFlow.map { hex ->
        HexColorPickerState(
            hex = "#$hex".uppercase(),
            color = fromHex(hex)
        )
    }


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