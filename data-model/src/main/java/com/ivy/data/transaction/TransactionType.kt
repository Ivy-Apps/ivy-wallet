package com.ivy.data.transaction

import com.ivy.data.account.Account

sealed class TransactionType {
    object Expense : TransactionType()

    object Income : TransactionType()

    data class Transfer(
        val toAccount: Account,
        val toAmount: Double
    ) : TransactionType()
}