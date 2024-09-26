package com.ivy.features

import kotlinx.collections.immutable.ImmutableList

data class FeaturesUiState(
    val featureItemViewStates: ImmutableList<FeatureItemViewState>,
)

sealed interface FeatureItemViewState {
    data class FeatureToggleViewState(
        val key: String,
        val name: String,
        val enabled: Boolean,
        val description: String?,
    ) : FeatureItemViewState

    data class FeatureHeaderViewState(val name: String) : FeatureItemViewState
}
