package com.ivy.domain.features

interface Features {
    val sortCategoriesAscending: BoolFeature
    val compactAccountsMode: BoolFeature
    val compactCategoriesMode: BoolFeature
    val showTitleSuggestions: BoolFeature
    val showCategorySearchBar: BoolFeature
    val hideTotalBalance: BoolFeature
    val showDecimalNumber: BoolFeature
    val standardKeypadLayout: BoolFeature
    val showAccountColorsInTransactions: BoolFeature

    val allFeatures: List<BoolFeature>
}
