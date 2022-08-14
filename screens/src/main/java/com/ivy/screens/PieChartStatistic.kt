package com.ivy.screens

import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class PieChartStatistic(
    val type: TrnType,
    val filterExcluded: Boolean = true,
    val accountList: List<UUID> = Collections.emptyList(),
    val transactions: List<TransactionOld> = Collections.emptyList(),
    val treatTransfersAsIncomeExpense: Boolean = false
) : Screen