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
        AttributionItem.Attribution(name = "iconsax", link = "https://iconsax.io"),
        AttributionItem.Attribution(
            name = "Material Symbols Google",
            link = "https://fonts.google.com/icons"
        ),
        AttributionItem.Attribution(
            name = "coolicons",
            link = "https://github.com/krystonschwarze/coolicons"
        ),
        AttributionItem.Divider(sectionName = "Fonts"),
        AttributionItem.Attribution(
            name = "Open Sans",
            link = "https://fonts.google.com/specimen/Open+Sans"
        ),
        AttributionItem.Attribution(
            name = "Raleway",
            link = "https://fonts.google.com/specimen/Raleway?query=raleway"
        ),
        AttributionItem.Attribution(
            name = "Nunito Sans",
            link = "https://fonts.google.com/specimen/Nunito+Sans?query=nunito"
        )
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributionItems)
    }

    override fun onEvent(event: AttributionsEvent) {}
}