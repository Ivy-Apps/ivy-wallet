package com.ivy.core.features

interface Features {
    val homeV2: BoolFeature
    val compactTransactions: BoolFeature

    val allFeatures: List<BoolFeature>
}