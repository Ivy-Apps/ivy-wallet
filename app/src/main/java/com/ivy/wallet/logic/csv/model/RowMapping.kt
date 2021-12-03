package com.ivy.wallet.logic.csv.model

import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction

data class RowMapping(
    val type: Int? = null,
    val amount: Int,

    val account: Int,
    val accountCurrency: Int? = null,
    val accountOrderNum: Int? = null,
    val accountColor: Int? = null,
    val accountIcon: Int? = null,

    val date: Int,
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

    val transformTransaction: (Transaction, Category?) -> Transaction = { transaction, _ ->
        transaction
    },

    val transformAmount: (Double, TransactionType) -> TransactionType = { _ , transactionType ->
        transactionType
    },

    val joinTransactions: (List<Transaction>) -> Pair<List<Transaction>, Int> = { transactions ->
        Pair(transactions,0)
    }
)