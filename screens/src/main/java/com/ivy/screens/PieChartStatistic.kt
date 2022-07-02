package com.ivy.screens

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class PieChartStatistic(
    val type: TransactionType,
    val filterExcluded: Boolean = true,
    val accountList: List<UUID> = Collections.emptyList(),
    val transactions: List<Transaction> = Collections.emptyList(),
    val treatTransfersAsIncomeExpense: Boolean = false
) : Screen