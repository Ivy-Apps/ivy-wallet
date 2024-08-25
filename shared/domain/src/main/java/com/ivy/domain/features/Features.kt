package com.ivy.domain.features

interface Features {
    val sortCategoriesAlphabetically: BoolFeature
    val showCompactAccounts: BoolFeature

    val allFeatures: List<BoolFeature>
}
