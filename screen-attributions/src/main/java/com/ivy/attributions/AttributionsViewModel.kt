package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    private val attributions = listOf(
        Attribution(section = "Icons", name = "Icon1", link = "www.google.com"),
        Attribution(section = "Icons", name = "Icon2", link = "www.google.com"),
        Attribution(section = "Typography", name = "Typography1", link = "www.google.com"),
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributions)
    }

    override fun onEvent(event: AttributionsEvent) {}
}