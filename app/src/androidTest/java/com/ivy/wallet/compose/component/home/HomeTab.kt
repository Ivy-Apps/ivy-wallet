package com.ivy.wallet.compose.component.home

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.PieChartScreen
import com.ivy.wallet.compose.component.edittrn.ChooseCategoryModal
import com.ivy.wallet.compose.component.edittrn.screen.TransactionScreen
import com.ivy.wallet.compose.component.edittrn.screen.TransferScreen
import com.ivy.wallet.compose.util.printTree
import com.ivy.wallet.compose.util.scroll
import com.ivy.wallet.utils.format

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
            .performScrollTo()
            .assertTextEquals(currency, amount, amountDecimal)
        return this
    }


    fun <N : TransactionScreen> clickTransaction(
        amount: String,
        title: String? = null,
        account: String? = null,
        category: String? = null,

        next: N
    ): N {
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

        composeTestRule.printTree()

        composeTestRule.onNode(
            matcher = matcher,
            useUnmergedTree = true
        )
            .assertIsDisplayed()
            .performClick()

        return next
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
        scroll(
            container = homeLazyColumn(),
            toKey = "upcoming_section"
        )

        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        )
            .performScrollTo()
            .performClick()
        return this
    }

    fun clickTransactionPay(): HomeTab {
        val matcher = hasText("Pay")
            .and(hasAnyAncestor(hasTestTag("transaction_card")))

        scroll(
            container = homeLazyColumn(),
            toMatcher = matcher
        )

        composeTestRule.onNode(
            matcher
        )
            .performScrollTo()
            .performClick()
        return this
    }

    fun clickTransactionSkip(): HomeTab {
        val matcher = hasText("Skip")
            .and(hasAnyAncestor(hasTestTag("transaction_card")))

        scroll(
            container = homeLazyColumn(),
            toMatcher = matcher
        )

        composeTestRule.onNode(
            matcher
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

    //-------- TransactionFlow -------------------

    fun addIncome(
        amount: Double,
        title: String? = null,
        category: String? = null,
        account: String = "Cash",
        description: String? = null,
    ): HomeTab {
        return clickAddFAB()
            .clickAddIncome()
            .addTransaction(
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
    ): HomeTab {
        return clickAddFAB()
            .clickAddExpense()
            .addTransaction(
                amount = amount,
                title = title,
                category = category,
                account = account,
                description = description
            )
    }

    private fun TransactionScreen.addTransaction(
        amount: Double,
        title: String?,
        category: String?,
        description: String?,
        account: String = "Cash"
    ): HomeTab {
        return firstOpen()
            .selectAccount(account)
            .enterNumber(
                number = amount.format(2),
                next = ChooseCategoryModal(composeTestRule)
            )
            .run {
                if (category != null) {
                    selectCategory(category, next = this@addTransaction)
                } else {
                    skip(next = this@addTransaction)
                }
            }.apply {
                if (title != null) {
                    editTitle(title)
                }
            }.apply {
                if (description != null) {
                    addDescription(description)
                }
            }.clickAdd(next = HomeTab(composeTestRule))
    }

    fun addTransfer(
        amount: Double,
        title: String? = null,
        fromAccount: String,
        toAccount: String
    ): HomeTab {
        return clickAddFAB()
            .clickAddTransfer()
            .firstOpen()
            .enterNumber(
                number = amount.format(2),
                next = TransferScreen(composeTestRule)
            ).selectFromAccount(fromAccount)
            .selectToAccount(toAccount)
            .apply {
                if (title != null) {
                    editTitle(title)
                }
            }.clickAdd(next = HomeTab(composeTestRule))
    }

    private fun homeLazyColumn(): SemanticsNodeInteraction =
        composeTestRule.onNodeWithTag("home_lazy_column")
}