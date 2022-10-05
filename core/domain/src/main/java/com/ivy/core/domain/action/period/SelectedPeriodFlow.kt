package com.ivy.core.domain.action.period

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.startdayofmonth.StartDayOfMonthFlow
import com.ivy.core.domain.pure.time.currentMonthlyPeriod
import com.ivy.core.domain.pure.time.monthlyPeriod
import com.ivy.data.time.SelectedPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @return a flow of the currently selected period [SelectedPeriod].
 */
@Singleton
class SelectedPeriodFlow @Inject constructor(
    private val startDayOfMonthFlow: StartDayOfMonthFlow,
    private val selectedPeriodSignal: SelectedPeriodSignal,
    private val timeProvider: TimeProvider,
) : SharedFlowAction<SelectedPeriod>() {
    override fun initialValue(): SelectedPeriod =
        currentMonthlyPeriod(startDayOfMonth = 1, timeProvider = timeProvider)

    override fun createFlow(): Flow<SelectedPeriod> = combine(
        startDayOfMonthFlow(), selectedPeriodSignal.receive()
    ) { startDayOfMonth, selectedPeriod ->
        if (selectedPeriod is SelectedPeriod.Monthly) {
            monthlyPeriod(
                dateInPeriod = selectedPeriod.range.to.minusDays(2).toLocalDate(),
                startDayOfMonth = startDayOfMonth
            )
        } else selectedPeriod
    }.flowOn(Dispatchers.Default)
}