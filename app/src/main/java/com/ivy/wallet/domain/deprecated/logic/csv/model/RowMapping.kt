package com.ivy.wallet.domain.deprecated.logic.csv.model

import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction

data class RowMapping(
    val type: Int? = null,
    val defaultTypeToExpense: Boolean = false,
    val amount: Int,

    val account: Int,
    val accountCurrency: Int? = null,
    val accountOrderNum: Int? = null,
    val accountColor: Int? = null,
    val accountIcon: Int? = null,

    val date: Int,
    val dateOnlyFormat: String? = null,
    val dateTimeFormat: String? = null,
    val timeOnly: Int? = null,
    val dueDate: Int? = null,

    val transferAmount: Int? = null,
    val toAccount: Int? = null,
    val toAmount: Int? = null,
    val toAccountCurrency: Int? = null,
    val toAccountColor: Int? = null,
    val toAccountOrderNum: Int? = null,
    val toAccountIcon: Int? = null,

    val category: Int?,
    val categoryOrderNum: Int? = null,
    val categoryColor: Int? = null,
    val categoryIcon: Int? = null,

    val title: Int?,
    val description: Int? = null,
    val id: Int? = null,

    /**
     * @param transaction - the final mapped transaction
     * @param category - category object because Transaction#categoryId but no Category
     * @param csvAmount - the original amount from the CSV file (can be negative, too)
     */
    val transformTransaction: (Transaction, Category?, csvAmount: Double) -> Transaction =
        { transaction, _, _ ->
            transaction
        },

    val joinTransactions: (List<Transaction>) -> JoinResult = { transactions ->
        JoinResult(transactions = transactions, mergedCount = 0)
    }
)

data class JoinResult(
    val transactions: List<Transaction>,
    val mergedCount: Int
)