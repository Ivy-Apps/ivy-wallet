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
    // TODO: AM/PM <-> 24h logic is too complex and messy! Consider refactoring!
    // TODO: Very shitty code!!!

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
    private val initialSelected = MutableStateFlow(initialUi.selected)

    override val uiFlow: Flow<TimePickerState> = combine(
        selected,
        initialSelected,
        amPm
    ) { selected24, initialSelected, amPm ->
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
                    (if (initialSelected.hour == 0) 12 else initialSelected.hour) - 1
                    // -1 because indexes start from 0 and AM/PM doesn't!
                }
                AmPm.PM -> {
                    /*
                    Possible values: 1..11
                    Indexes: 0..10
                     */
                    (initialSelected.hour % 12) - 1 // -1 because indexes start from 0 and AM/PM doesn't!
                }
                null -> {
                    /*
                    Possible values: 0..23
                    Indexes: 0..23
                     */
                    initialSelected.hour
                }
            }.coerceIn(0..hours.lastIndex),
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
        updateAmPm(event.initialTime.hour)
        initialSelected.value = event.initialTime
        selected.value = event.initialTime
    }

    private fun handleHourChange(event: TimePickerEvent.HourChange) {
        val pickedHour = event.pickerHour.value

        // transform the input picker hour to 24h format (0-23)
        val newHour24 = when (amPm.value) {
            AmPm.AM -> if (pickedHour == 12) 0 else pickedHour
            AmPm.PM -> pickedHour + 12
            null -> pickedHour
        }

        updateHour(newHour24)
    }

    private fun updateAmPm(newHour24: Int): AmPm? {
        val newAmPm = if (!uses24HourFormat(appContext)) {
            if (newHour24 < 12) AmPm.AM else AmPm.PM
        } else null
        amPm.value = newAmPm
        return newAmPm
    }

    private fun handleAmPmChange(event: TimePickerEvent.AmPmChange) {
        amPm.value = event.amPm
        val newHour24 = when (event.amPm) {
            AmPm.AM -> if (selected.value.hour == 0) 12 else selected.value.hour
            AmPm.PM -> selected.value.hour + 12
        }
        updateHour(newHour24)
        initialSelected.value = selected.value
    }

    private fun updateHour(newHour24: Int) {
        selected.value = selected.value.withHour(newHour24.coerceIn(0..23))
    }

    private fun handleMinuteChange(event: TimePickerEvent.MinuteChange) {
        selected.value = selected.value.withMinute(event.minute.value)
    }

    // endregion
}