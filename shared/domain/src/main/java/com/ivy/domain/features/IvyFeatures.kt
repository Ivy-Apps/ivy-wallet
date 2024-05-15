package com.ivy.domain.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyFeatures @Inject constructor() : Features {
    override val homeV2 = BoolFeature(
        key = "home_v2",
        name = "Home screen v2",
        description = "Activate the new Home screen."
    )
    override val compactTransactions = BoolFeature(
        key = "compact_transaction",
        name = "Compact transactions",
        description = "Smaller transactions cards" +
            " so you can fit more on your screen."
    )
    override val sortCategoriesAlphabetically = BoolFeature(
        key = "sort_categories_alphabetically",
        name = "Sort Categories Alphabetically",
        description = "Sort income and expenses" +
        " categories alphabetically"
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            homeV2,
            compactTransactions,
            sortCategoriesAlphabetically
        )
}
