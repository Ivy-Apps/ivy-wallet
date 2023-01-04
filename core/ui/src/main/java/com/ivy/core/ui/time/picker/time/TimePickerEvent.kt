package com.ivy.core.ui.time.picker.time

import com.ivy.core.ui.time.picker.time.data.PickerHour
import com.ivy.core.ui.time.picker.time.data.PickerMinute
import java.time.LocalTime

sealed interface TimePickerEvent {
    data class Initial(val selected: LocalTime) : TimePickerEvent
    data class HourChange(val hour: PickerHour) : TimePickerEvent
    data class MinuteChange(val minute: PickerMinute) : TimePickerEvent
    data class AmPmChange(val amPm: AmPm) : TimePickerEvent
}