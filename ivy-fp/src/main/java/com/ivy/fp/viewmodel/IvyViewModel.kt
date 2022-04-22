package com.ivy.fp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class IvyViewModel<S> : ViewModel() {
    protected abstract val mutableState: MutableStateFlow<S>

    fun state(): StateFlow<S> = mutableState.readOnly()
    protected fun stateVal(): S = state().value

    protected suspend fun updateState(update: suspend (S) -> S) {
        mutableState.value = update(stateVal())
    }

    protected fun updateStateNonBlocking(update: (S) -> S) {
        mutableState.value = update(stateVal())
    }
}