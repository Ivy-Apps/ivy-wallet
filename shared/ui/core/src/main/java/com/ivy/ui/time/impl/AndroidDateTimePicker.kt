package com.ivy.ui.time.impl

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivy.base.time.TimeProvider
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class AndroidDateTimePicker @Inject constructor(
    private val timeProvider: TimeProvider,
) : DateTimePicker {
    private var datePickerViewState by mutableStateOf<DatePickerViewState?>(null)
    private var timePickerViewState by mutableStateOf<TimePickerViewState?>(null)

    @Composable
    override fun Content() {
        datePickerViewState?.let { DatePicker(it) }
        timePickerViewState?.let { TimePicker(it) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePicker(
        viewState: DatePickerViewState,
        modifier: Modifier = Modifier
    ) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = viewState.initialDate?.toEpochMilli(),
        )
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = { datePickerViewState = null },
            confirmButton = {
                ConfirmButton(
                    onClick = {
                        datePickerViewState = null
                        pickerState.selectedDateMillis
                            ?.let(Instant::ofEpochMilli)
                            ?.let(viewState.onDatePicked)
                    }
                )
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimePicker(
        viewState: TimePickerViewState,
        modifier: Modifier = Modifier
    ) {
        val time = viewState.initialTime ?: timeProvider.localNow().toLocalTime()
        val pickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
        )
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = { timePickerViewState = null },
            confirmButton = {
                ConfirmButton(
                    onClick = {
                        timePickerViewState = null
                        viewState.onTimePicked(
                            LocalTime.of(pickerState.hour, pickerState.minute)
                        )
                    }
                )
            }
        ) {
            TimePicker(state = pickerState)
        }
    }

    @Composable
    fun ConfirmButton(
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        Button(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(text = "Select")
        }
    }

    override fun pickDate(initialDate: Instant?, onDatePick: (Instant) -> Unit) {
        datePickerViewState = DatePickerViewState(
            initialDate = initialDate,
            onDatePicked = onDatePick
        )
    }

    override fun pickTime(initialTime: LocalTime?, onTimePick: (LocalTime) -> Unit) {
        timePickerViewState = TimePickerViewState(
            initialTime = initialTime,
            onTimePicked = onTimePick
        )
    }

    @Immutable
    data class DatePickerViewState(
        val initialDate: Instant?,
        val onDatePicked: (Instant) -> Unit
    )

    @Immutable
    data class TimePickerViewState(
        val initialTime: LocalTime?,
        val onTimePicked: (LocalTime) -> Unit
    )
}