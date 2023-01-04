package com.ivy.core.ui.time.picker.time

import android.annotation.SuppressLint
import android.content.Context
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.uses24HourFormat
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.ui.time.picker.time.data.PickerHour
import com.ivy.core.ui.time.picker.time.data.PickerMinute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * https://en.wikipedia.org/wiki/12-hour_clock
 */
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class TimePickerViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    timeProvider: TimeProvider
) : SimpleFlowViewModel<TimePickerState, TimePickerEvent>() {

    override val initialUi = TimePickerState(
        amPm = null,
        hours = emptyList(),
        hoursListSize = 0,
        minutes = emptyList(),
        minutesListSize = 0,
        selected = timeProvider.timeNow().toLocalTime(),
        selectedHour = 0,
    )

    private val amPm = MutableStateFlow(initialUi.amPm)
    private val selected = MutableStateFlow(initialUi.selected)

    override val uiFlow: Flow<TimePickerState> = combine(
        selected,
        amPm
    ) { selected, amPm ->
        val hours = when (amPm) {
            AmPm.AM -> 1..12
            AmPm.PM -> 1..11
            null -> 0..23
        }.map {
            PickerHour(
                value = it,
                text = it.toString().padStart(2, '0'),
            )
        }
        val minutes = (0..59).map {
            PickerMinute(
                value = it,
                text = it.toString().padStart(2, '0'),
            )
        }

        TimePickerState(
            amPm = amPm,
            hours = hours,
            hoursListSize = hours.size,
            minutes = minutes,
            minutesListSize = minutes.size,
            selected = selected,
            selectedHour = when (amPm) {
                AmPm.AM -> {
                    // in the case of AM selected hour must be <= 12
                    if (selected.hour == 0) 12 else selected.hour
                }
                AmPm.PM -> {
                    // in the case of PM selected hour must be > 12 with max of 23
                    selected.hour % 12
                }
                null -> selected.hour
            }
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: TimePickerEvent) = when (event) {
        is TimePickerEvent.Initial -> handleInitial(event)
        is TimePickerEvent.HourChange -> handleHourChange(event)
        is TimePickerEvent.AmPmChange -> handleAmPmChange(event)
        is TimePickerEvent.MinuteChange -> handleMinuteChange(event)
    }

    private fun handleInitial(event: TimePickerEvent.Initial) {
        selected.value = event.selected
        amPm.value = if (uses24HourFormat(appContext)) {
            if (event.selected.hour < 12) AmPm.AM else AmPm.PM
        } else null
    }

    private fun handleHourChange(event: TimePickerEvent.HourChange) {
        val newHour = when (amPm.value) {
            AmPm.AM -> if (event.hour.value == 12) 0 else event.hour.value
            AmPm.PM -> event.hour.value + 12
            null -> event.hour.value
        }

        selected.value = selected.value.withHour(newHour)
    }

    private fun handleAmPmChange(event: TimePickerEvent.AmPmChange) {
        amPm.value = event.amPm
    }

    private fun handleMinuteChange(event: TimePickerEvent.MinuteChange) {
        selected.value = selected.value.withMinute(event.minute.value)
    }

    // endregion
}