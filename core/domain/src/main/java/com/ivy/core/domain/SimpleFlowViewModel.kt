package com.ivy.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class SimpleFlowViewModel<UiState, Event> : FlowViewModel<Unit, UiState, Event>() {
    override val initialState = Unit
    override val stateFlow: Flow<Unit> = flow {}
}