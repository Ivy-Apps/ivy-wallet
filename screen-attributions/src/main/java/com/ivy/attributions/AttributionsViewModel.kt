package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.domain.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor(
    private val attributionsProvider: AttributionsProvider
) :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributionsProvider.provideAttributions())
    }

    override fun onEvent(event: AttributionsEvent) {}
}