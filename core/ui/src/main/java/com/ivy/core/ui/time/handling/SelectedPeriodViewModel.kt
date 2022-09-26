package com.ivy.core.ui.time.handling

import android.content.Context
import com.ivy.core.domain.action.FlowViewModel
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.action.period.SelectedPeriodSignal
import com.ivy.core.domain.pure.time.currentMonthlyPeriod
import com.ivy.core.ui.action.mapping.MapSelectedPeriodAct
import com.ivy.core.ui.data.SelectedPeriodUi
import com.ivy.data.time.SelectedPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("StaticFieldLeak")
@HiltViewModel
class SelectedPeriodViewModel @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val mapSelectedPeriodAct: MapSelectedPeriodAct,
    private val selectedPeriodSignal: SelectedPeriodSignal
) : FlowViewModel<SelectedPeriod, SelectedPeriodUi, PeriodModalEvent>() {
    override fun initialState(): SelectedPeriod =
        currentMonthlyPeriod(context = appContext, startDayOfMonth = 1)

    override fun initialUiState(): SelectedPeriodUi = SelectedPeriodUi.Monthly("")

    override fun stateFlow(): Flow<SelectedPeriod> = selectedPeriodFlow()

    override suspend fun mapToUiState(state: SelectedPeriod): SelectedPeriodUi =
        mapSelectedPeriodAct(state)

    override suspend fun handleEvent(event: PeriodModalEvent) {
        TODO("Not yet implemented")
    }
}