package com.ivy.core.ui.color.picker.custom

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class HexColorPickerState(
    val hex: String,
    val color: Color?
)