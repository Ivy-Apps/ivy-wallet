package com.ivy.wallet.logic.csv.model

data class RowMapping(
    val type: Int,
    val amount: Int,

    val account: Int,
    val accountCurrency: Int? = null,
    val accountOrderNum: Int? = null,
    val accountColor: Int? = null,
    val accountIcon: Int? = null,

    val date: Int,
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
    val id: Int? = null
)