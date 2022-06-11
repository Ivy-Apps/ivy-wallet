package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.printTree

class HomeTab(
    private val composeTestRule: IvyComposeTestRule
) : MainBottomBar<AddFABMenu>(composeTestRule) {
    fun openMoreMenu(): HomeMoreMenu {
        composeTestRule.onNodeWithTag("home_more_menu_arrow")
            .performClick()
        return HomeMoreMenu(composeTestRule)
    }

    fun assertBalance(
        amount: String,
        amountDecimal: String,
        currency: String = "USD"
    ): HomeTab {
        composeTestRule.onNodeWithTag("home_balance")
            .assertTextEquals(currency, amount, amountDecimal)
        return this
    }

    fun clickTransaction(
        amount: String,
        title: String? = null,
        account: String? = null,
        category: String? = null
    ): TransactionScreen {
        var matcher = hasTestTag("type_amount_currency")
            .and(hasAnyDescendant(hasText(amount)))

        if (account != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasAnyDescendant(
                        hasText(account)
                    )
                )
            )
        }

        if (category != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasAnyDescendant(
                        hasText(category)
                    )
                )
            )
        }

        if (title != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasText(title)
                )
            )
        }

        composeTestRule.printTree(
            useUnmergedTree = true
        )

        composeTestRule.onNode(
            matcher = matcher,
            useUnmergedTree = true
        )
            .assertIsDisplayed()
            .performClick()

        return TransactionScreen(composeTestRule)
    }

    fun assertTransactionNotExists(
        amount: String
    ): HomeTab {
        composeTestRule.onNode(
            hasTestTag("transaction_card")
                .and(hasText(amount))
        ).assertDoesNotExist()

        return this
    }

    fun assertUpcomingIncome(
        amount: String,
        currency: String
    ): HomeTab {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_income",
            useUnmergedTree = true
        ).assertTextEquals("$amount $currency")

        return this
    }

    fun assertUpcomingExpense(
        amount: String,
        currency: String
    ): HomeTab {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_expense",
            useUnmergedTree = true
        ).assertTextEquals("$amount $currency")

        return this
    }

    fun dismissPrompt(): HomeTab {
        composeTestRule.onNodeWithContentDescription("prompt_dismiss")
            .performClick()
        return this
    }

    fun assertUpcomingDoesNotExist(): HomeTab {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).assertDoesNotExist()
        return this
    }

    fun clickUpcoming(): HomeTab {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).performClick()
        return this
    }

    fun clickTransactionPay(): HomeTab {
        composeTestRule.onNode(
            hasText("Pay")
                .and(hasAnyAncestor(hasTestTag("transaction_card")))
        )
            .performScrollTo()
            .performClick()
        return this
    }

    fun assertGreeting(
        greeting: String
    ): HomeTab {
        composeTestRule.onNodeWithTag("home_greeting_text", useUnmergedTree = true)
            .assertTextEquals(greeting)
        return this
    }

    fun clickIncomeCard(): PieChartScreen {
        composeTestRule.onNodeWithTag("home_card_income")
            .performClick()
        return PieChartScreen(composeTestRule)
    }

    fun clickExpenseCard(): PieChartScreen {
        composeTestRule.onNodeWithTag("home_card_expense")
            .performClick()
        return PieChartScreen(composeTestRule)
    }

    override fun clickAddFAB(): AddFABMenu {
        return clickAddFAB(next = AddFABMenu(composeTestRule))
    }
}