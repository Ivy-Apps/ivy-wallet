package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.base.format

class TransactionFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val chooseCategoryModal = ChooseCategoryModal(composeTestRule)

    fun addIncome(
        amount: Double,
        title: String?,
        category: String?
    ) {
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddIncome()

        addTransaction(
            amount = amount,
            title = title,
            category = category
        )
    }

    fun addExpense(
        amount: Double,
        title: String?,
        category: String?
    ) {
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddExpense()

        addTransaction(
            amount = amount,
            title = title,
            category = category
        )
    }

    private fun addTransaction(
        amount: Double,
        title: String?,
        category: String?
    ) {
        amountInput.enterNumber(amount.format(2))

        if (category != null) {
            chooseCategoryModal.selectCategory(category)
        } else {
            chooseCategoryModal.skip()
        }

        if (title != null) {
            composeTestRule.onNodeWithTag("input_field")
                .performTextInput(title)
        }

        composeTestRule.onNodeWithText("Add")
            .performClick()
    }
}