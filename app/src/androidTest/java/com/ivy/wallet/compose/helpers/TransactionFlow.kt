package com.ivy.wallet.compose.helpers

import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.utils.format

class TransactionFlow(
    private val composeTestRule: IvyComposeTestRule
) {
    fun HomeTab.addIncome(
        amount: Double,
        title: String? = null,
        category: String? = null,
        account: String = "Cash",
        description: String? = null,
    ) {
        clickAddFAB()
            .clickAddIncome()
            .addTransaction(
                amount = amount,
                title = title,
                category = category,
                account = account,
                description = description,
            )
    }

    fun HomeTab.addExpense(
        amount: Double,
        title: String? = null,
        category: String? = null,
        account: String = "Cash",
        description: String? = null,
    ) {
        clickAddFAB()
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
    ) {
        firstOpen()
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
            }.clickAdd()
    }

    fun HomeTab.addTransfer(
        amount: Double,
        title: String? = null,
        fromAccount: String,
        toAccount: String
    ) {
        clickAddFAB()
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
            }.clickAdd()
    }
}