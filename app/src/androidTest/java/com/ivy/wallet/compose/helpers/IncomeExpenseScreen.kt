package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class IncomeExpenseScreen(
    composeTestRule: IvyComposeTestRule
) : TransactionScreen(composeTestRule) {

    fun editCategory(
        currentCategory: String,
        newCategory: String
    ): IncomeExpenseScreen {
        return clickCategory(currentCategory)
            .selectCategory(
                categoryName = newCategory,
                next = this
            )
    }

    fun clickCategory(currentCategory: String): ChooseCategoryModal {
        composeTestRule.onNodeWithText(currentCategory)
            .performClick()
        return ChooseCategoryModal(composeTestRule)
    }
}