package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    private val attributions = listOf(
        Attributions(section = "Icons", name = "Icon1", link = ""),
        Attributions(section = "Icons", name = "Icon2", link = ""),
        Attributions(section = "Typography", name = "Typography1", link = ""),
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributions)
    }

    override fun onEvent(event: AttributionsEvent) {}
}