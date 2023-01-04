package com.ivy.core.ui.time.picker.time.data

import androidx.compose.runtime.Immutable

@Immutable
data class PickerHour(
    val text: String,
    val value: Int,
)