package com.ivy.domain.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyFeatures @Inject constructor() : Features {

    override val sortCategoriesAlphabetically = BoolFeature(
        key = "sort_categories_alphabetically",
        group = FeatureGroup.Category,
        name = "Sort Categories Alphabetically",
        description = "Sort income and expenses" +
                " categories alphabetically"
    )

    override val compactAccountsMode = BoolFeature(
        key = "compact_account_ui",
        group = FeatureGroup.Account,
        name = "Compact account UI",
        description = "Enables more compact and dense UI for the \"Accounts\" tab"
    )

    override val compactCategoriesMode = BoolFeature(
        key = "compact_categories_ui",
        group = FeatureGroup.Category,
        name = "Compact category UI",
        description = "Activates a more streamlined and space-efficient interface for the \"Categories\" Screen"
    )

    override val showTitleSuggestions = BoolFeature(
        key = "show_title_suggestions",
        group = FeatureGroup.Other,
        name = "Show previous title suggestions",
        description = "Enables display of previous transaction titles when editing or creating a new transaction",
        defaultValue = true
    )

    override val showCategorySearchBar = BoolFeature(
        key = "search_categories",
        group = FeatureGroup.Category,
        name = "Search categories",
        description = "Show search bar in category screen",
        defaultValue = true
    )

    override val hideTotalBalance = BoolFeature(
        key = "hide_total_balance",
        group = FeatureGroup.Account,
        name = "Hide total balance",
        description = "Enable hide the total balance from the accounts tab",
        defaultValue = false
    )

    override val showDecimalNumber = BoolFeature(
        key = "show_decimal_number",
        group = FeatureGroup.Other,
        name = "Show Decimal Number",
        description = "Whether to show the decimal part in amounts",
        defaultValue = true
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            sortCategoriesAlphabetically,
            compactAccountsMode,
            compactCategoriesMode,
            showTitleSuggestions,
            showCategorySearchBar,
            hideTotalBalance,
            /* will be uncommented when this functionality
             * will be available across the application in up-coming PRs
            showDecimalNumber
             */
        )
}
