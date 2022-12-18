package com.ivy.core.ui.time.picker.date

import androidx.compose.runtime.Immutable

@Immutable
data class DatePickerState(
    val days: List<String>,
    val months: List<String>,
)