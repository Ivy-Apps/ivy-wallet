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

    override val compactAccountsMode = BoolFeature(
        key = "compact_account_ui",
        name = "Compact account UI",
        description = "Enables more compact and dense UI for the \"Accounts\" tab"
    )

    override val compactCategoriesMode = BoolFeature(
        key = "compact_categories_ui",
        name = "Compact category UI",
        description = "Activates a more streamlined and space-efficient interface for the \"Categories\" Screen"
    )

    override val showTitleSuggestions = BoolFeature(
        key = "show_title_suggestions",
        name = "Show previous title suggestions",
        description = "Enables display of previous transaction titles when editing or creating a new transaction",
        defaultValue = true
    )


    override val showCategorySearchBar = BoolFeature(
        key = "show_category_search_bar",
        name = "Show category search bar",
        description = "Show search bar in category screen",
        defaultValue = false
    )

    override val hideTotalBalance = BoolFeature(
        key = "hide_total_balance",
        name = "Hide total balance",
        description = "Enable hide the total balance from the accounts tab",
        defaultValue = false
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            sortCategoriesAlphabetically,
            compactAccountsMode,
            compactCategoriesMode,
            showTitleSuggestions,
            showCategorySearchBar,
            hideTotalBalance
        )
}
