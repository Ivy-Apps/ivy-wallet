package com.ivy.domain.features

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IvyFeatures @Inject constructor() : Features {

    override val sortCategoriesAscending = BoolFeature(
        key = "sort_categories_ascending",
        group = FeatureGroup.Category,
        name = "Sort categories list",
        description = "Show categories in ascending order (A-Z) on the transaction entry screen",
        defaultValue = false
    )

    override val compactAccountsMode = BoolFeature(
        key = "compact_account_ui",
        group = FeatureGroup.Account,
        name = "Compact account cards",
        description = "Make the Accounts tab UI more compact and dense",
        defaultValue = false
    )

    override val compactCategoriesMode = BoolFeature(
        key = "compact_category_ui",
        group = FeatureGroup.Category,
        name = "Compact category cards",
        description = "Simplified design of the Categories screen",
        defaultValue = false
    )

    override val showTitleSuggestions = BoolFeature(
        key = "show_title_suggestions",
        group = FeatureGroup.Other,
        name = "Show previous title suggestions",
        description = "Suggest past transaction titles when creating a new entry",
        defaultValue = true
    )

    override val showCategorySearchBar = BoolFeature(
        key = "search_categories",
        group = FeatureGroup.Category,
        name = "Search within categories",
        description = "Display a search bar on the Categories screen",
        defaultValue = true
    )

    override val hideTotalBalance = BoolFeature(
        key = "hide_total_balance",
        group = FeatureGroup.Account,
        name = "Hide account total balance",
        description = "Hide total balance summary on the Accounts screen",
        defaultValue = false
    )

    override val showDecimalNumber = BoolFeature(
        key = "show_decimal_number",
        group = FeatureGroup.Other,
        name = "Show values with decimals",
        description = "Include the decimal part in amounts",
        defaultValue = true
    )

    override val standardKeypadLayout = BoolFeature(
        key = "enable_standard_keypad_layout",
        group = FeatureGroup.Other,
        name = "Standard keypad layout",
        description = "Replace numeric keypad with standard phone layout",
        defaultValue = false
    )

    override val showAccountColorsInTransactions = BoolFeature(
        key = "show_account_color",
        group = FeatureGroup.Other,
        name = "Colorful account labels",
        description = "Display account-specific colors in transactions",
        defaultValue = false
    )

    override val allFeatures: List<BoolFeature>
        get() = listOf(
            sortCategoriesAscending,
            compactAccountsMode,
            compactCategoriesMode,
            showTitleSuggestions,
            showCategorySearchBar,
            hideTotalBalance,
            standardKeypadLayout,
            showAccountColorsInTransactions
            /* will be uncommented when this functionality
             * will be available across the application in up-coming PRs
            showDecimalNumber
             */
        )
}
