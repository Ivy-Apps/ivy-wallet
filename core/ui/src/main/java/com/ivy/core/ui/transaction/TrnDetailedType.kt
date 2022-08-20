package com.ivy.core.ui.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivy.common.timeNowUTC
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime

enum class TrnDetailedType {
    Income, Transfer, ActualExpense, UpcomingExpense, OverdueExpense
}

@Composable
fun detailedType(
    type: TransactionType,
    time: TrnTime,
): TrnDetailedType = remember(type, time) {
    when (type) {
        is TransactionType.Income -> TrnDetailedType.Income
        is TransactionType.Expense -> {
            when (time) {
                is TrnTime.Due -> if (time.due.isAfter(timeNowUTC()))
                    TrnDetailedType.UpcomingExpense else TrnDetailedType.OverdueExpense
                is TrnTime.Actual -> TrnDetailedType.ActualExpense
            }
        }
        is TransactionType.Transfer -> TrnDetailedType.Transfer
    }
}