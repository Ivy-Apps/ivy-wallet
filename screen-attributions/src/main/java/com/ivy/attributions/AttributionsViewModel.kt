package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState()
    }

    override fun onEvent(event: AttributionsEvent) {}
}