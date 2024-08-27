package com.ivy.domain.features

interface Features {
    val sortCategoriesAlphabetically: BoolFeature
    val compactAccountsMode: BoolFeature

    val allFeatures: List<BoolFeature>
}
