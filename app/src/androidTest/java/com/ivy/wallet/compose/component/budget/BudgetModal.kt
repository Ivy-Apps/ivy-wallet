package com.ivy.wallet.compose.component.budget

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DeleteConfirmationModal
import com.ivy.wallet.compose.component.DeleteItem
import com.ivy.wallet.compose.component.amountinput.AmountInput
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput

class BudgetModal(
    private val composeTestRule: IvyComposeTestRule
) : AmountInput<BudgetModal>, DeleteItem<BudgetsScreen> {
    private val amountInput = IvyAmountInput(composeTestRule)

    fun enterName(budgetName: String): BudgetModal {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(budgetName)
        return this
    }

    private fun clickBudgetAmount(): IvyAmountInput {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()
        return IvyAmountInput(composeTestRule)
    }

    fun clickCategory(category: String): BudgetModal {
        composeTestRule.onNode(
            hasText(category)
                .and(hasAnyAncestor(hasTestTag("budget_categories_row"))),
            useUnmergedTree = true
        ).performClick()
        return this
    }

    fun clickAdd(): BudgetsScreen {
        composeTestRule.onNodeWithText("Add")
            .performClick()
        return BudgetsScreen(composeTestRule)
    }

    fun clickSave(): BudgetsScreen {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return BudgetsScreen(composeTestRule)
    }

    private fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("modal_delete")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    fun clickClose(): BudgetsScreen {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()
        return BudgetsScreen(composeTestRule)
    }

    override fun enterAmount(number: String): BudgetModal {
        return clickBudgetAmount()
            .enterNumber(number, next = this)
    }

    override fun deleteWithConfirmation(): BudgetsScreen {
        return clickDelete()
            .confirmDelete(next = BudgetsScreen(composeTestRule))
    }
}