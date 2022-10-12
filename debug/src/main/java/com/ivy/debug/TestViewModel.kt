package com.ivy.debug

import com.ivy.core.SimpleFlowViewModel
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
    selectedPeriodFlow: SelectedPeriodFlow,
    private val mapSelectedPeriodAct: MapSelectedPeriodUiAct
) : SimpleFlowViewModel<TestStateUi, Unit>() {
    override val initialUi: TestStateUi = TestStateUi(
        selectedPeriodUi = SelectedPeriodUi.AllTime(
            btnText = "",
            rangeUi = TimeRangeUi(allTime(), "", "")
        )
    )

    override val uiFlow: Flow<TestStateUi> = selectedPeriodFlow().map {
        TestStateUi(
            selectedPeriodUi = mapSelectedPeriodAct(it)
        )
    }

    override suspend fun handleEvent(event: Unit) {}
}