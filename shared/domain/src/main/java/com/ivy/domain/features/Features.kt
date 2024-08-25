package com.ivy.domain.features

interface Features {
    val sortCategoriesAlphabetically: BoolFeature
    val compactAccounts: BoolFeature

    val allFeatures: List<BoolFeature>
}
