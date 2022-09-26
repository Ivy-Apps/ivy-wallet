package com.ivy.core.ui.time.handling

import com.ivy.core.domain.action.HandlerViewModel
import com.ivy.core.domain.action.period.SetSelectedPeriodAct
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedPeriodHandlerViewModel @Inject constructor(
    private val setSelectedPeriodAct: SetSelectedPeriodAct
) : HandlerViewModel<PeriodModalEvent>() {

    override suspend fun handleEvent(event: PeriodModalEvent) {
        TODO("Not yet implemented")
    }
}