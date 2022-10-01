package com.ivy.core.ui.color.picker

import androidx.compose.ui.graphics.Color
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.pure.ui.groupByRows
import com.ivy.core.ui.color.picker.data.ColorSectionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ColorPickerViewModel @Inject constructor() :
    FlowViewModel<ColorPickerState, ColorPickerState, ColorPickerEvent>() {
    companion object {
        const val COLORS_PER_ROW = 5
    }

    override fun initialState(): ColorPickerState = ColorPickerState(
        sections = listOf()
    )

    override fun initialUiState(): ColorPickerState = initialState()


    override fun stateFlow(): Flow<ColorPickerState> = colorSectionsFlow().map { sections ->
        ColorPickerState(
            sections = sections,
        )
    }

    private fun colorSectionsFlow(): Flow<List<ColorSectionUi>> = flowOf(
        listOf(
            ColorSectionUi(
                name = "Colors",
                colorRows = colorRows(colors())
            ),
            ColorSectionUi(
                name = "Color Light",
                colorRows = colorRows(lightColors())
            ),
            ColorSectionUi(
                name = "Colors Dark",
                colorRows = colorRows(darkColors())
            ),
        )
    )

    private fun colorRows(colors: List<Color>): List<List<Color>> =
        groupByRows(colors, iconsPerRow = COLORS_PER_ROW)

    override suspend fun mapToUiState(state: ColorPickerState): ColorPickerState = state


    // region Event Handling
    override suspend fun handleEvent(event: ColorPickerEvent) {}
    // endregion
}