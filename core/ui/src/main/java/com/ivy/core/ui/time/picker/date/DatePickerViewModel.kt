package com.ivy.core.ui.time.picker.date

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import com.ivy.core.ui.time.picker.date.data.PickerYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DatePickerViewModel @Inject constructor(
    timeProvider: TimeProvider
) : SimpleFlowViewModel<DatePickerState, DatePickerEvent>() {
    override val initialUi = DatePickerState(
        days = emptyList(),
        daysListSize = 0,
        months = emptyList(),
        monthsListSize = 0,
        years = emptyList(),
        yearsListSize = 0,
        selected = timeProvider.dateNow()
    )

    private val selectedDate = MutableStateFlow(initialUi.selected)

    override val uiFlow: Flow<DatePickerState> = selectedDate.map { selected ->
        val days = (1..selected.month.maxLength()).map { PickerDay(it.toString(), it) }
        val months = listOf(
            PickerMonth("Jan", 1),
            PickerMonth("Feb", 2),
            PickerMonth("Mar", 3),
            PickerMonth("Apr", 4),
            PickerMonth("May", 5),
            PickerMonth("Jun", 6),
            PickerMonth("Jul", 7),
            PickerMonth("Aug", 8),
            PickerMonth("Sep", 9),
            PickerMonth("Oct", 10),
            PickerMonth("Nov", 11),
            PickerMonth("Dec", 12),
        )

        val years = (selected.year - 10..selected.year + 10).map {
            PickerYear(it.toString(), it)
        }

        DatePickerState(
            days = days,
            daysListSize = days.size,
            months = months,
            monthsListSize = months.size,
            years = years,
            yearsListSize = years.size,
            selected = selected,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: DatePickerEvent) = when (event) {
        is DatePickerEvent.Initial -> handleInitial(event)
        is DatePickerEvent.DayChange -> handleDayChange(event)
        is DatePickerEvent.MonthChange -> handleMonthChange(event)
        is DatePickerEvent.YearChange -> handleYearChange(event)
    }

    private fun handleInitial(event: DatePickerEvent.Initial) {
        selectedDate.value = event.selected
    }

    private fun handleDayChange(event: DatePickerEvent.DayChange) {
        selectedDate.value = selectedDate.value.withDayOfMonth(event.day.value)
    }

    private fun handleMonthChange(event: DatePickerEvent.MonthChange) {
        selectedDate.value = selectedDate.value.withMonth(event.month.value)
    }

    private fun handleYearChange(event: DatePickerEvent.YearChange) {
        selectedDate.value = selectedDate.value.withYear(event.year.value)
    }
    // endregion
}