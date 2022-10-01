package com.ivy.core.ui.color

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.color.data.ColorSectionUi

@Immutable
internal data class ColorPickerState(
    val sections: List<ColorSectionUi>
)