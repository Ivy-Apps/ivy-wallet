package com.ivy.core

import com.ivy.core.domain.FlowViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class SimpleFlowViewModel<UiState, Event> : FlowViewModel<Unit, UiState, Event>() {
    override val initialInternal = Unit
    override val internalFlow: Flow<Unit> = flow {}
}