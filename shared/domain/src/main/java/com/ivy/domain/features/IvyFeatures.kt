package com.ivy.domain.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyFeatures @Inject constructor() : Features {
    override val sortCategoriesAlphabetically = BoolFeature(
        key = "sort_categories_alphabetically",
        name = "Sort Categories Alphabetically",
        description = "Sort income and expenses" +
                " categories alphabetically"
    )
    override val showCompactAccounts = BoolFeature(
        key = "show_compact_accounts",
        name = "Show Compact Accounts",
        description = "Loads accounts in Dense Account mode"
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            sortCategoriesAlphabetically,
            showCompactAccounts
        )
}
