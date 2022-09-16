package com.ivy.core.ui.transaction.util
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import com.ivy.common.timeNowUTC
//import com.ivy.data.transaction.TrnTime
//import java.time.LocalDateTime
//
//enum class TrnDetailedType {
//    ActualIncome, Transfer, ActualExpense,
//    UpcomingExpense, OverdueExpense,
//    UpcomingIncome, OverdueIncome,
//}
//
//@Composable
//internal fun detailedType(
//    type: TransactionType,
//    time: TrnTime,
//): TrnDetailedType = remember(type, time) {
//    fun isOverdue(due: LocalDateTime): Boolean = due.isAfter(timeNowUTC())
//
//    when (type) {
//        is TransactionType.Income -> when (time) {
//            is TrnTime.Due -> if (isOverdue(time.due))
//                TrnDetailedType.UpcomingIncome else TrnDetailedType.OverdueIncome
//            is TrnTime.Actual -> TrnDetailedType.ActualIncome
//        }
//        is TransactionType.Expense -> {
//            when (time) {
//                is TrnTime.Due -> if (isOverdue(time.due))
//                    TrnDetailedType.UpcomingExpense else TrnDetailedType.OverdueExpense
//                is TrnTime.Actual -> TrnDetailedType.ActualExpense
//            }
//        }
//        is TransactionType.Transfer -> TrnDetailedType.Transfer
//    }
//}