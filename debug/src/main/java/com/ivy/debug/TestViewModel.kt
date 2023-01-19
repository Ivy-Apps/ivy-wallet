package com.ivy.debug

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.pure.time.allTime
import com.ivy.core.ui.action.mapping.MapSelectedPeriodUiAct
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.TimeRangeUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    selectedPeriodFlow: SelectedPeriodFlow,
    private val mapSelectedPeriodAct: MapSelectedPeriodUiAct,
    private val writeBaseCurrencyAct: WriteBaseCurrencyAct,
    baseCurrencyFlow: BaseCurrencyFlow,
) : SimpleFlowViewModel<TestStateUi, TestEvent>() {
    override val initialUi: TestStateUi = TestStateUi(
        selectedPeriodUi = SelectedPeriodUi.AllTime(
            periodBtnText = "",
            rangeUi = TimeRangeUi(allTime(), "", "")
        ),
        baseCurrency = ""
    )

    override val uiFlow: Flow<TestStateUi> = combine(
        selectedPeriodFlow(), baseCurrencyFlow()
    ) { period, baseCurrency ->
        TestStateUi(
            selectedPeriodUi = mapSelectedPeriodAct(period),
            baseCurrency = baseCurrency,
        )
    }

    // region Event handling
    override suspend fun handleEvent(event: TestEvent) = when (event) {
        is TestEvent.BaseCurrencyChange -> handleBaseCurrencyChange(event)
    }

    private suspend fun handleBaseCurrencyChange(event: TestEvent.BaseCurrencyChange) {
        writeBaseCurrencyAct(event.currency)
    }
    // endregion
}