package com.ivy.ui.time.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class AndroidDateTimePicker @Inject constructor(
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
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
                ConfirmButton(onClick = {
                    datePickerViewState = null
                    pickerState.selectedDateMillis?.let(Instant::ofEpochMilli)
                        ?.let {
                            with(timeConverter) { it.toLocalDate() }
                    }?.let(viewState.onDatePicked)
                })
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            DatePicker(
                state = pickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    todayContentColor = MaterialTheme.colorScheme.onBackground,
                    todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                    dayContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
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
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                TimePicker(
                    modifier = Modifier.padding(16.dp),
                    state = pickerState,
                    colors = TimePickerDefaults.colors(
                        selectorColor = MaterialTheme.colorScheme.primary,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
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

    override fun pickDate(initialDate: Instant?, onDatePick: (LocalDate) -> Unit) {
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
        val onDatePicked: (LocalDate) -> Unit
    )

    @Immutable
    data class TimePickerViewState(
        val initialTime: LocalTime?,
        val onTimePicked: (LocalTime) -> Unit
    )
}