package com.ivy.wallet.ui.settings.experimental

import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.settings.preference.PreferenceAct
import com.ivy.wallet.domain.action.settings.preference.SetPreferenceAct
import com.ivy.wallet.domain.data.preference.NewEditScreenPref
import com.ivy.wallet.domain.data.preference.SmallTrnsPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ExperimentalViewModel @Inject constructor(
    private val smallTrnsPrefAct: PreferenceAct<SmallTrnsPref, Boolean>,
    private val setSmallTrnsPrefAct: SetPreferenceAct<SmallTrnsPref, Boolean>,

    private val newEditScreenPrefAct: PreferenceAct<NewEditScreenPref, Boolean>,
    private val setNewEditScreenPrefAct: SetPreferenceAct<NewEditScreenPref, Boolean>
) : FRPViewModel<ExpState, ExpEvent>() {
    override val _state: MutableStateFlow<ExpState> = MutableStateFlow(ExpState.Initial)

    override suspend fun handleEvent(event: ExpEvent): suspend () -> ExpState = when (event) {
        ExpEvent.Load -> load(Unit)
        is ExpEvent.SetSmallTrnsPref -> setSmallTrnsPref(event)
        is ExpEvent.SetNewEditPref -> setNewEditPref(event)
    }

    private fun load(unit: Unit) = loadSmallTrnsPref() then ::loadNewEditScreenPref

    private fun loadSmallTrnsPref() = SmallTrnsPref() asParamTo smallTrnsPrefAct then { value ->
        updateLoaded {
            it.copy(smallTrnsPref = value ?: false)
        }
    }

    private suspend fun loadNewEditScreenPref(state: ExpState) = NewEditScreenPref() asParamTo
            newEditScreenPrefAct thenInvokeAfter { value ->
        updateLoaded {
            it.copy(newEditScreen = value ?: false)
        }
    }

    private suspend fun updateLoaded(update: (ExpState.Loaded) -> ExpState.Loaded) = updateState {
        when (it) {
            ExpState.Initial -> update(
                ExpState.Loaded(
                    smallTrnsPref = false,
                    newEditScreen = false
                )
            )
            is ExpState.Loaded -> update(it)
        }
    }

    private suspend fun setSmallTrnsPref(event: ExpEvent.SetSmallTrnsPref) =
        SmallTrnsPref(value = event.newValue) asParamTo setSmallTrnsPrefAct thenInvokeAfter ::load

    private suspend fun setNewEditPref(event: ExpEvent.SetNewEditPref) =
        NewEditScreenPref(value = event.newValue) asParamTo
                setNewEditScreenPrefAct thenInvokeAfter ::load
}