package com.ivy.core.ui.time.picker.date

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import com.ivy.core.ui.time.picker.date.data.PickerYear
import java.time.LocalDate

@Immutable
data class DatePickerState(
    val days: List<PickerDay>,
    val daysListSize: Int,
    val months: List<PickerMonth>,
    val monthsListSize: Int,
    val years: List<PickerYear>,
    val yearsListSize: Int,

    val selectedContext: String,
    val selected: LocalDate,
)