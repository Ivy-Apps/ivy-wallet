package com.ivy.core.domain.action.period

import com.ivy.core.domain.action.Action
import com.ivy.data.time.SelectedPeriod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetSelectedPeriodAct @Inject constructor(
    private val selectedPeriodSignal: SelectedPeriodSignal
) : Action<SelectedPeriod, Unit>() {
    override fun dispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    override suspend fun action(input: SelectedPeriod) {
        selectedPeriodSignal.send(input)
    }
}