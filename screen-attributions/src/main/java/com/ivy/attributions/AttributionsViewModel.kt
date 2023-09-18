package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    private val attributions = listOf<AttributionItem>(
        AttributionItem.Divider(section = "Icons"),
        AttributionItem.Attribution(name = "Icon1", link = "https://www.google.com"),
        AttributionItem.Attribution(name = "Icon2", link = "https://www.google.com"),
        AttributionItem.Divider(section = "Typography"),
        AttributionItem.Attribution(name = "Typography1", link = "https://www.google.com"),
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributions)
    }

    override fun onEvent(event: AttributionsEvent) {}
}