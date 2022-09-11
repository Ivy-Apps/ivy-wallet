package com.ivy.core.action.time

import android.content.Context
import com.ivy.core.action.SharedFlowAction
import com.ivy.core.functions.time.currentMonthlyPeriod
import com.ivy.core.functions.time.dateToSelectedMonthlyPeriod
import com.ivy.data.time.SelectedPeriod
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gets currently selected period.
 */
@Singleton
class SelectedPeriodFlow @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val startDayOfMonthFlow: StartDayOfMonthFlow,
    private val selectedPeriodSignal: SelectedPeriodSignal,
) : SharedFlowAction<SelectedPeriod>() {
    override fun initialValue(): SelectedPeriod =
        currentMonthlyPeriod(context = appContext, startDayOfMonth = 1)

    override fun createFlow(): Flow<SelectedPeriod> = combine(
        startDayOfMonthFlow(), selectedPeriodSignal.receive()
    ) { startDayOfMonth, selectedPeriod ->
        if (selectedPeriod is SelectedPeriod.Monthly) {
            dateToSelectedMonthlyPeriod(
                context = appContext,
                dateInPeriod = selectedPeriod.period.to.minusDays(2).toLocalDate(),
                startDayOfMonth = startDayOfMonth
            )
        } else selectedPeriod
    }.flowOn(Dispatchers.Default)


}