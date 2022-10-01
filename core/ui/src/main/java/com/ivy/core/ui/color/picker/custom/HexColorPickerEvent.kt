package com.ivy.core.ui.color.picker.custom

import androidx.compose.ui.graphics.Color

sealed interface HexColorPickerEvent {
    data class SetColor(val color: Color) : HexColorPickerEvent
    data class Hex(val hex: String) : HexColorPickerEvent
}