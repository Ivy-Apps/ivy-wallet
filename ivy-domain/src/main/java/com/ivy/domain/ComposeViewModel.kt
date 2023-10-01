package com.ivy.domain

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

/**
 * A simple base ViewModel utilizing Compose' reactivity.
 */
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
}