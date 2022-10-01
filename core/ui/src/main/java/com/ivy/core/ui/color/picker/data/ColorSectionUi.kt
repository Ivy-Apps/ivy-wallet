package com.ivy.core.ui.color.picker.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorSectionUi(
    val name: String,
    val colorRows: List<List<Color>>
)