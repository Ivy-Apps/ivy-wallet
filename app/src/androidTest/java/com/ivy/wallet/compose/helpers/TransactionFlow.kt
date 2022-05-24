package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.hideKeyboard
import com.ivy.wallet.utils.format

class TransactionFlow<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val chooseCategoryModal = ChooseCategoryModal(composeTestRule)

    fun addIncome(
        amount: Double,
        title: String? = null,
        category: String? = null,
        account: String = "Cash",
        description: String? = null,
    ) {
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddIncome()

        addTransaction(
            amount = amount,
            title = title,
            category = category,
            account = account,
            description = description,
        )
    }

    fun addExpense(
        amount: Double,
        title: String? = null,
        category: String? = null,
        account: String = "Cash",
        description: String? = null,
    ) {
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddExpense()

        addTransaction(
            amount = amount,
            title = title,
            category = category,
            account = account,
            description = description
        )
    }

    private fun addTransaction(
        amount: Double,
        title: String?,
        category: String?,
        description: String?,
        account: String = "Cash"
    ) {
        composeTestRule.onNode(
            hasTestTag("amount_modal_account")
                .and(hasText(account))
        ).performClick()

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

        if (description != null) {
            composeTestRule.hideKeyboard()

            composeTestRule.onNodeWithText("Add description")
                .performClick()

            composeTestRule.onNode(
                hasTestTag("modal_desc_input"),
                useUnmergedTree = true
            ).performTextReplacement(description)

            composeTestRule.onNodeWithTag("modal_desc_save")
                .performClick()
        }

        composeTestRule.onNodeWithText("Add")
            .performClick()
    }

    fun addTransfer(
        amount: Double,
        title: String? = null,
        fromAccount: String,
        toAccount: String
    ) {
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddTransfer()

        amountInput.enterNumber(amount.format(2))

        composeTestRule.onNode(
            hasTestTag("from_account")
                .and(hasText(fromAccount))
        ).performClick()

        composeTestRule.onNode(
            hasTestTag("to_account")
                .and(hasText(toAccount))
        ).performClick()

        if (title != null) {
            composeTestRule.onNodeWithTag("input_field")
                .performTextInput(title)
        }

        composeTestRule.onNodeWithText("Add")
            .performClick()
    }
}