package com.ivy.features

import kotlinx.collections.immutable.ImmutableList

data class FeaturesUiState(
    val features: ImmutableList<FeatureUi>,
)

data class FeatureUi(
    val name: String,
    val enabled: Boolean,
    val description: String?,
)