package com.ivy.wallet.compose.component.edittrn.screen

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.edittrn.ChooseCategoryModal

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