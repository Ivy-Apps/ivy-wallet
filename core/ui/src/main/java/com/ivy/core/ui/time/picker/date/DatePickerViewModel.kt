package com.ivy.core.ui.time.picker.date

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

) : SimpleFlowViewModel<DatePickerState, DatePickerEvent>() {
    override val initialUi = DatePickerState(
        days = emptyList(),
        daysListSize = 0,
        months = emptyList(),
        monthsListSize = 0,
        years = emptyList(),
        yearsListSize = 0,
    )

    private val selectedMonthNumber = MutableStateFlow(1)

    override val uiFlow: Flow<DatePickerState> = selectedMonthNumber.map { month ->
        val days = (1..31).map { PickerDay(it.toString(), it) }
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

        val years = listOf(
            PickerYear("2018", 2018),
            PickerYear("2019", 2019),
            PickerYear("2020", 2020),
            PickerYear("2021", 2021),
            PickerYear("2022", 2022),
            PickerYear("2023", 2023),
            PickerYear("2024", 2024),
            PickerYear("2025", 2025),
            PickerYear("2026", 2026),
            PickerYear("2027", 2027),
            PickerYear("2028", 2028),
            PickerYear("2029", 2029),
            PickerYear("2030", 2030),
        )

        DatePickerState(
            days = days,
            daysListSize = days.size,
            months = months,
            monthsListSize = months.size,
            years = years,
            yearsListSize = years.size,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: DatePickerEvent) = when (event) {
        is DatePickerEvent.DayChange -> handleDayChange(event)
        is DatePickerEvent.MonthChange -> handleMonthChange(event)
        is DatePickerEvent.YearChange -> handleYearChange(event)
    }

    private fun handleDayChange(event: DatePickerEvent.DayChange) {
        // TODO:
    }

    private fun handleMonthChange(event: DatePickerEvent.MonthChange) {
        // TODO:
    }

    private fun handleYearChange(event: DatePickerEvent.YearChange) {
        // TODO:
    }
    // endregion
}