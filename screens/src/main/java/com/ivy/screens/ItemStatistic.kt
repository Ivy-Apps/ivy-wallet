package com.ivy.screens

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class ItemStatistic(
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val unspecifiedCategory: Boolean? = false,
    val transactionType: TransactionType? = null,
    val accountIdFilterList: List<UUID> = Collections.emptyList(),
    val transactions: List<Transaction> = Collections.emptyList()
) : Screen