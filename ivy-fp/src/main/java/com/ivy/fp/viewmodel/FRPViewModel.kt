package com.ivy.fp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class FRPViewModel<S, E> : ViewModel() {
    protected abstract val _state: MutableStateFlow<S>

    abstract suspend fun handleEvent(event: E): suspend () -> S

    fun onEvent(event: E) {
        viewModelScope.launch {
            _state.value = handleEvent(event).invoke()
        }
    }

    fun state(): StateFlow<S> = _state.readOnly()
    protected fun stateVal(): S = state().value

    protected suspend fun updateState(update: suspend (S) -> S) {
        _state.value = update(stateVal())
    }

    protected fun updateStateNonBlocking(update: (S) -> S) {
        _state.value = update(stateVal())
    }
}