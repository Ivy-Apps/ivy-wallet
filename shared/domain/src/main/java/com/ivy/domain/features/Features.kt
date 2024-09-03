package com.ivy.domain.features

interface Features {
    val sortCategoriesAlphabetically: BoolFeature
    val compactAccountsMode: BoolFeature
    val compactCategoriesMode: BoolFeature
    val showTitleSuggestions: BoolFeature

    val allFeatures: List<BoolFeature>
}
