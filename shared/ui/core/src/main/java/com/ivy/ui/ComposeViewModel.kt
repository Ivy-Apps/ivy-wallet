package com.ivy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A simple base ViewModel utilizing Compose' reactivity.
 */
@Stable
abstract class ComposeViewModel<UiState, UiEvent> : ViewModel() {
    /**
     * Optimized for Compose ui state.
     * Use only Compose primitives and immutable structures.
     * @return optimized for Compose ui state.
     */
    @Composable
    abstract fun uiState(): UiState

    /**
     * Sends an event of an action that happened
     * in the UI to be processed in the ViewModel.
     */
    abstract fun onEvent(event: UiEvent)

    private val observableEvents = MutableLiveData<ViewModelEvent>()

    fun observeViewModelEvents(): LiveData<ViewModelEvent> = observableEvents

    protected fun postViewModelEvent(event: ViewModelEvent) {
        observableEvents.postValue(event)
    }
}
