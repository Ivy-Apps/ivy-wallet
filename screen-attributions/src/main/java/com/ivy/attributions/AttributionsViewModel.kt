package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    private val attributionItems = listOf<AttributionItem>(
        AttributionItem.Divider(sectionName = "Icons"),
        AttributionItem.Attribution(name = "Icon1", link = "https://www.google.com"),
        AttributionItem.Attribution(name = "Icon2", link = "https://www.google.com"),
        AttributionItem.Divider(sectionName = "Typography"),
        AttributionItem.Attribution(name = "Typography1", link = "https://www.google.com"),
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributionItems)
    }

    override fun onEvent(event: AttributionsEvent) {}
}