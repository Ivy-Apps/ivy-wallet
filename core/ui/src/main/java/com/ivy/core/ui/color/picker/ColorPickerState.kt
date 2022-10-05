package com.ivy.core.ui.color.picker

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.color.picker.data.ColorSectionUi

@Immutable
internal data class ColorPickerState(
    val sections: List<ColorSectionUi>
)