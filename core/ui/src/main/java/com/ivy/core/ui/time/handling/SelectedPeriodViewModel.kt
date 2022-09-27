package com.ivy.core.ui.time.handling

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Immutable
import com.ivy.common.atEndOfDay
import com.ivy.common.dateNowLocal
import com.ivy.common.timeNowLocal
import com.ivy.core.domain.action.FlowViewModel
import com.ivy.core.domain.action.period.SetSelectedPeriodAct
import com.ivy.core.domain.action.settings.startdayofmonth.StartDayOfMonthFlow
import com.ivy.core.domain.pure.time.*
import com.ivy.core.ui.data.period.MonthUi
import com.ivy.core.ui.data.period.monthsList
import com.ivy.core.ui.time.handling.SelectedPeriodViewModel.State
import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SelectedPeriodViewModel @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val startDayOfMonthFlow: StartDayOfMonthFlow,
    private val setSelectedPeriodAct: SetSelectedPeriodAct
) : FlowViewModel<State, State, PeriodModalEvent>() {

    @Immutable
    data class State(
        val startDayOfMonth: Int,
        val months: List<MonthUi>,
    )

    override fun initialState(): State = State(startDayOfMonth = 1, months = emptyList())

    override fun initialUiState(): State = initialState()

    override fun stateFlow(): Flow<State> = startDayOfMonthFlow().map {
        val currentYear = dateNowLocal().year

        State(
            startDayOfMonth = it,
            months = monthsList(appContext, year = currentYear - 1, currentYear = false) +
                    monthsList(appContext, year = currentYear, currentYear = true) +
                    monthsList(appContext, year = currentYear + 1, currentYear = false)
        )
    }

    override suspend fun mapToUiState(state: State): State = state

    override suspend fun handleEvent(event: PeriodModalEvent) {
        val selectedPeriod = when (event) {
            PeriodModalEvent.AllTime -> SelectedPeriod.AllTime(allTime())
            is PeriodModalEvent.CustomRange -> SelectedPeriod.CustomRange(event.period)
            is PeriodModalEvent.InTheLast -> toSelectedPeriod(event)
            is PeriodModalEvent.Monthly -> dateToSelectedMonthlyPeriod(
                // TODO: Refactor that
                // 10 is a safe date in the middle of the month
                dateInPeriod = LocalDate.of(event.month.year, event.month.number, 10),
                startDayOfMonth = state.value.startDayOfMonth
            )
            PeriodModalEvent.ResetToCurrentPeriod ->
                currentMonthlyPeriod(startDayOfMonth = state.value.startDayOfMonth)
            PeriodModalEvent.LastYear -> yearPeriod(dateNowLocal().year - 1)
            PeriodModalEvent.ThisYear -> yearPeriod(dateNowLocal().year)
        }

        setSelectedPeriodAct(selectedPeriod)
    }

    private fun toSelectedPeriod(event: PeriodModalEvent.InTheLast): SelectedPeriod.InTheLast {
        val now = timeNowLocal()
        val n = event.n
        return SelectedPeriod.InTheLast(
            n = n,
            unit = event.unit,
            period = Period.FromTo(
                // n - 1 because we count today
                // Negate: -n because we want to start from the **last** N unit
                from = shiftTime(time = now, n = -(n - 1), unit = event.unit),
                to = dateNowLocal().atEndOfDay(),
            )
        )
    }
}