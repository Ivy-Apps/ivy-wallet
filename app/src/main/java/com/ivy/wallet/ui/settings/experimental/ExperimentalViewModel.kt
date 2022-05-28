package com.ivy.wallet.ui.settings.experimental

import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.settings.preference.PreferenceAct
import com.ivy.wallet.domain.action.settings.preference.SetPreferenceAct
import com.ivy.wallet.domain.data.preference.SmallTrnsPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ExperimentalViewModel @Inject constructor(
    private val smallTrnsPrefAct: PreferenceAct<SmallTrnsPref, Boolean>,
    private val setSmallTrnsPrefAct: SetPreferenceAct<SmallTrnsPref, Boolean>
) : FRPViewModel<ExpState, ExpEvent>() {
    override val _state: MutableStateFlow<ExpState> = MutableStateFlow(ExpState.Initial)

    override suspend fun handleEvent(event: ExpEvent): suspend () -> ExpState = when (event) {
        ExpEvent.Load -> load(Unit)
        is ExpEvent.SetSmallTrnsPref -> setSmallTrnsPref(event)
    }

    private fun load(unit: Unit) = SmallTrnsPref() asParamTo smallTrnsPrefAct then { smallTrns ->
        updateState {
            ExpState.Loaded(
                smallTrnsPref = smallTrns ?: false
            )
        }
    }

    private suspend fun setSmallTrnsPref(event: ExpEvent.SetSmallTrnsPref) =
        SmallTrnsPref(value = event.newValue) asParamTo setSmallTrnsPrefAct thenInvokeAfter ::load
}