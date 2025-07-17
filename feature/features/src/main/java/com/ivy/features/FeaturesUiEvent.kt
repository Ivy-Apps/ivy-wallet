package com.ivy.features

sealed interface FeaturesUiEvent {
    data class ToggleFeature(val key: String) : FeaturesUiEvent
}
