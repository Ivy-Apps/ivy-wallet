package com.ivy.core.ui.time.picker.time

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.time.picker.time.data.PickerHour
import com.ivy.core.ui.time.picker.time.data.PickerMinute
import java.time.LocalTime

@Immutable
data class TimePickerState(
    val amPm: AmPm?,
    val hours: List<PickerHour>,
    val hoursListSize: Int,
    val minutes: List<PickerMinute>,
    val minutesListSize: Int,

    val selectedHourIndex: Int,
    val selected: LocalTime,
)

@Immutable
enum class AmPm {
    AM,
    PM,
}