package com.ivy.core.ui.color.picker

import androidx.compose.ui.graphics.Color
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.pure.ui.groupByRows
import com.ivy.core.ui.color.picker.data.ColorSectionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ColorPickerViewModel @Inject constructor() :
    SimpleFlowViewModel<ColorPickerState, ColorPickerEvent>() {
    companion object {
        const val COLORS_PER_ROW = 5
    }

    override val initialUi = ColorPickerState(
        sections = listOf()
    )

    override val uiFlow: Flow<ColorPickerState> = colorSectionsFlow().map { sections ->
        ColorPickerState(
            sections = sections,
        )
    }

    private fun colorSectionsFlow(): Flow<List<ColorSectionUi>> = flowOf(
        listOf(
            ColorSectionUi(
                name = "Primary",
                colorRows = colorRows(colors())
            ),
            ColorSectionUi(
                name = "Light",
                colorRows = colorRows(lightColors())
            ),
            ColorSectionUi(
                name = "Dark",
                colorRows = colorRows(darkColors())
            ),
        )
    )

    private fun colorRows(colors: List<Color>): List<List<Color>> =
        groupByRows(colors, itemsPerRow = COLORS_PER_ROW)


    // region Event Handling
    override suspend fun handleEvent(event: ColorPickerEvent) {}
    // endregion
}
