package com.ivy.wallet.functional.wallet

import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.TransactionDao

//TODO: history(range)
//TODO: overdue(range)
//TODO: upcoming(range)


suspend fun history(
    transactionDao: TransactionDao,
    range: ClosedTimeRange
): List<Transaction> {
    return transactionDao.findAllBetween(
        startDate = range.from,
        endDate = range.to
    )
}

fun List<Transaction>.withDateDividers(

): List<TransactionHistoryItem> {

    TODO()
}

//fun List<Transaction>.withDateDividers(
//    exchangeRatesLogic: ExchangeRatesLogic,
//    settingsDao: SettingsDao,
//    accountDao: AccountDao
//): List<TransactionHistoryItem> {
//    val trns = this
//    if (trns.isEmpty()) return trns
//
//    val historyWithDividers = mutableListOf<TransactionHistoryItem>()
//
//    val dateTransactionsMap = mutableMapOf<LocalDate, MutableList<Transaction>>()
//    for (transaction in trns) {
//        if (transaction.dateTime != null) {
//            val date = transaction.dateTime.convertUTCtoLocal().toLocalDate()
//            dateTransactionsMap[date]?.add(transaction) ?: run {
//                dateTransactionsMap[date] = mutableListOf(transaction)
//            }
//        }
//    }
//
//    dateTransactionsMap.toSortedMap { date1, date2 ->
//        (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
//    }.forEach { (date, trns) ->
//        historyWithDividers.add(
//            TransactionHistoryDateDivider(
//                date = date,
//                income = trns
//                    .filter { it.type == TransactionType.INCOME }
//                    .sumInBaseCurrency(
//                        exchangeRatesLogic = exchangeRatesLogic,
//                        settingsDao = settingsDao,
//                        accountDao = accountDao
//                    ),
//                expenses = trns
//                    .filter { it.type == TransactionType.EXPENSE }
//                    .sumInBaseCurrency(
//                        exchangeRatesLogic = exchangeRatesLogic,
//                        settingsDao = settingsDao,
//                        accountDao = accountDao
//                    )
//            )
//        )
//
//        historyWithDividers.addAll(trns)
//    }
//
//    return historyWithDividers
//}