package com.ivy.core.ui.time.handling

import com.ivy.common.timeNowLocal
import com.ivy.core.domain.action.FlowViewModel
import com.ivy.core.domain.action.period.SetSelectedPeriodAct
import com.ivy.core.domain.action.settings.startdayofmonth.StartDayOfMonthFlow
import com.ivy.core.domain.pure.time.allTime
import com.ivy.core.domain.pure.time.dateToSelectedMonthlyPeriod
import com.ivy.core.domain.pure.time.shiftTime
import com.ivy.core.ui.time.handling.SelectedPeriodHandlerViewModel.State
import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SelectedPeriodHandlerViewModel @Inject constructor(
    private val startDayOfMonthFlow: StartDayOfMonthFlow,
    private val setSelectedPeriodAct: SetSelectedPeriodAct
) : FlowViewModel<State, State, PeriodModalEvent>() {

    data class State(
        val startDayOfMonth: Int,
    )

    override fun initialState(): State = State(startDayOfMonth = 1)

    override fun initialUiState(): State = initialState()

    override fun stateFlow(): Flow<State> = startDayOfMonthFlow().map {
        State(
            startDayOfMonth = it
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
            is PeriodModalEvent.SetPeriod -> TODO()
        }

        setSelectedPeriodAct(selectedPeriod)
    }

    private fun toSelectedPeriod(event: PeriodModalEvent.InTheLast): SelectedPeriod.InTheLast {
        val now = timeNowLocal()
        return SelectedPeriod.InTheLast(
            n = event.n,
            unit = event.unit,
            period = Period.FromTo(
                // -n because we want to start from the **last** N unit
                from = shiftTime(time = now, n = -event.n, unit = event.unit),
                to = now,
            )
        )
    }
}