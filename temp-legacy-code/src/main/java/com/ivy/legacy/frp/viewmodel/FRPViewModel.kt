package com.ivy.frp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Deprecated("Legacy. Use ComposeViewModel")
abstract class FRPViewModel<S, E> : ViewModel() {
    protected abstract val _state: MutableStateFlow<S>

    abstract suspend fun handleEvent(event: E): suspend () -> S

    fun onEvent(event: E) {
        viewModelScope.launch {
            TestIdlingResource.increment()
            _state.value = handleEvent(event).invoke()
            TestIdlingResource.decrement()
        }
    }

    fun state(): StateFlow<S> = _state.readOnly()
    protected fun stateVal(): S = state().value

    protected suspend fun updateState(update: suspend (S) -> S): S {
        _state.value = update(stateVal())
        return stateVal()
    }

    protected fun updateStateNonBlocking(update: (S) -> S): S {
        _state.value = update(stateVal())
        return stateVal()
    }
}