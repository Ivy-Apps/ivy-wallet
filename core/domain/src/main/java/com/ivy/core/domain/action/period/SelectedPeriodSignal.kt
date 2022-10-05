package com.ivy.core.domain.action.period

import android.content.Context
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.SignalFlow
import com.ivy.core.domain.pure.time.currentMonthlyPeriod
import com.ivy.data.time.SelectedPeriod
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedPeriodSignal @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val timeProvider: TimeProvider
) : SignalFlow<SelectedPeriod>() {
    override fun initialSignal(): SelectedPeriod =
        currentMonthlyPeriod(startDayOfMonth = 1, timeProvider = timeProvider)
}