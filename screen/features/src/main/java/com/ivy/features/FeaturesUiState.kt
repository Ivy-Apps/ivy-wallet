package com.ivy.features

import kotlinx.collections.immutable.ImmutableList

data class FeaturesUiState(
    val features: ImmutableList<Feature>,
)

data class FeatureItem(
    val key: String,
    val name: String,
    val enabled: Boolean,
    val description: String?,
) : Feature

data class FeatureHeader(val name: String) : Feature

interface Feature
