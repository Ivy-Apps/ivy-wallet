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
import java.time.LocalTime
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
    // TODO: AM/PM <-> 24h logic is too complex and messy! Consider refactoring!

    override val initialUi = TimePickerState(
        amPm = null,
        hours = emptyList(),
        hoursListSize = 0,
        minutes = emptyList(),
        minutesListSize = 0,
        selected = timeProvider.timeNow().toLocalTime(),
        selectedHourIndex = 0,
    )

    private val amPm = MutableStateFlow(initialUi.amPm)
    private val selected = MutableStateFlow(initialUi.selected)

    override val uiFlow: Flow<TimePickerState> = combine(
        selected,
        amPm
    ) { selected24, amPm ->
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
            selected = selected24,
            selectedHourIndex = when (amPm) {
                AmPm.AM -> {
                    /*
                    Possible values: 1..12
                    Indexes: 0..11
                     */
                    (if (selected24.hour == 0) 12 else selected24.hour) - 1
                    // -1 because indexes start from 0 and AM/PM doesn't!
                }
                AmPm.PM -> {
                    /*
                    Possible values: 1..11
                    Indexes: 0..10
                     */
                    (selected24.hour % 12) - 1 // -1 because indexes start from 0 and AM/PM doesn't!
                }
                null -> {
                    /*
                    Possible values: 0..23
                    Indexes: 0..23
                     */
                    selected24.hour
                }
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
        if (hourChanged(event.initialTime)) {
            selected.value = event.initialTime
            amPm.value = if (!uses24HourFormat(appContext)) {
                if (event.initialTime.hour < 12) AmPm.AM else AmPm.PM
            } else null
        }
    }

    private fun hourChanged(newHour24: LocalTime): Boolean = newHour24.withSecond(0)
        .withNano(0) !=
            selected.value
                .withSecond(0)
                .withNano(0)

    private fun handleHourChange(event: TimePickerEvent.HourChange) {
        val pickedHour = event.pickerHour.value

        // transform the input picker hour to 24h format (0-23)
        val newHour24 = when (amPm.value) {
            AmPm.AM -> if (pickedHour == 12) 0 else pickedHour
            AmPm.PM -> pickedHour + 12
            null -> pickedHour
        }

        selected.value = selected.value.withHour(newHour24)
    }

    private fun handleAmPmChange(event: TimePickerEvent.AmPmChange) {
        amPm.value = event.amPm
    }

    private fun handleMinuteChange(event: TimePickerEvent.MinuteChange) {
        selected.value = selected.value.withMinute(event.minute.value)
    }

    // endregion
}