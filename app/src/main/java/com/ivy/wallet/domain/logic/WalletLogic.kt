package com.ivy.wallet.domain.logic

import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.logic.currency.sumInBaseCurrency
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.filterOverdue
import com.ivy.wallet.ui.onboarding.model.filterUpcoming
import com.ivy.wallet.utils.beginningOfIvyTime
import com.ivy.wallet.utils.convertUTCtoLocal
import com.ivy.wallet.utils.toEpochSeconds
import java.time.LocalDate

@Deprecated("Migrate to FP Style")
class WalletLogic(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
) {
    fun history(range: FromToTimeRange): List<TransactionHistoryItem> {
        return transactionDao.findAllBetween(
            startDate = range.from(),
            endDate = range.to()
        ).withDateDividers(
            exchangeRatesLogic = exchangeRatesLogic,
            settingsDao = settingsDao,
            accountDao = accountDao
        )
    }

    fun calculateUpcomingIncome(range: FromToTimeRange): Double {
        return calculateIncome(upcomingTransactions(range))
    }

    fun calculateUpcomingExpenses(range: FromToTimeRange): Double {
        return calculateExpenses(upcomingTransactions(range))
    }

    fun calculateOverdueIncome(range: FromToTimeRange): Double {
        return calculateIncome(overdueTransactions(range))
    }

    fun calculateOverdueExpenses(range: FromToTimeRange): Double {
        return calculateExpenses(overdueTransactions(range))
    }

    fun calculateIncome(transactions: List<Transaction>): Double {
        return calculate(transactions, TransactionType.INCOME)
    }

    fun calculateExpenses(transactions: List<Transaction>): Double {
        return calculate(transactions, TransactionType.EXPENSE)
    }

    private fun calculate(transactions: List<Transaction>, trnType: TransactionType): Double {
        return transactions
            .filter { it.type == trnType }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun upcomingTransactions(range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetween(
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    fun overdueTransactions(range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetween(
            startDate = beginningOfIvyTime(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }
}

@Deprecated("Migrate to FP Style")
fun List<Transaction>.withDateDividers(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao
): List<TransactionHistoryItem> {
    val trns = this
    if (trns.isEmpty()) return trns

    val historyWithDividers = mutableListOf<TransactionHistoryItem>()

    val dateTransactionsMap = mutableMapOf<LocalDate, MutableList<Transaction>>()
    for (transaction in trns) {
        if (transaction.dateTime != null) {
            val date = transaction.dateTime.convertUTCtoLocal().toLocalDate()
            dateTransactionsMap[date]?.add(transaction) ?: run {
                dateTransactionsMap[date] = mutableListOf(transaction)
            }
        }
    }

    dateTransactionsMap.toSortedMap { date1, date2 ->
        (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
    }.forEach { (date, trns) ->
        historyWithDividers.add(
            TransactionHistoryDateDivider(
                date = date,
                income = trns
                    .filter { it.type == TransactionType.INCOME }
                    .sumInBaseCurrency(
                        exchangeRatesLogic = exchangeRatesLogic,
                        settingsDao = settingsDao,
                        accountDao = accountDao
                    ),
                expenses = trns
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumInBaseCurrency(
                        exchangeRatesLogic = exchangeRatesLogic,
                        settingsDao = settingsDao,
                        accountDao = accountDao
                    )
            )
        )

        historyWithDividers.addAll(trns)
    }

    return historyWithDividers
}