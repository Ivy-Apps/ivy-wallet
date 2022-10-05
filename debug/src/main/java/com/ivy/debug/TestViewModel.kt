package com.ivy.debug

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.pure.time.allTime
import com.ivy.core.ui.action.mapping.MapSelectedPeriodUiAct
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.TimeRangeUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val mapSelectedPeriodAct: MapSelectedPeriodUiAct
) : FlowViewModel<TestStateUi, TestStateUi, Unit>() {
    override fun initialState() = TestStateUi(
        selectedPeriodUi = SelectedPeriodUi.AllTime(
            btnText = "",
            rangeUi = TimeRangeUi(allTime(), "", "")
        )
    )

    override fun initialUiState(): TestStateUi = initialState()

    override fun stateFlow(): Flow<TestStateUi> = selectedPeriodFlow().map {
        TestStateUi(
            selectedPeriodUi = mapSelectedPeriodAct(it)
        )
    }

    override suspend fun mapToUiState(state: TestStateUi): TestStateUi = state

    override suspend fun handleEvent(event: Unit) {}
}