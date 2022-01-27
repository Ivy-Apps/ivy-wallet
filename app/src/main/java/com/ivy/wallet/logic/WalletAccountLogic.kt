package com.ivy.wallet.logic

import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.TransactionDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.filterOverdue
import com.ivy.wallet.ui.onboarding.model.filterUpcoming
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.absoluteValue

class WalletAccountLogic(
    private val transactionDao: TransactionDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao
) {

    fun adjustBalance(
        account: Account,
        actualBalance: Double = calculateAccountBalance(account),
        newBalance: Double,

        adjustTransactionTitle: String = "Adjust balance",

        isFiat: Boolean? = null,
        trnIsSyncedFlag: Boolean = false, //TODO: Remove this once Bank Integration trn sync is properly implemented
    ) {
        val diff = actualBalance - newBalance

        val finalDiff = if (isFiat == true && abs(diff) < 0.009) 0.0 else diff
        when {
            finalDiff < 0 -> {
                //add income
                transactionDao.save(
                    Transaction(
                        type = TransactionType.INCOME,
                        title = adjustTransactionTitle,
                        amount = diff.absoluteValue,
                        dateTime = timeNowUTC(),
                        accountId = account.id,
                        isSynced = trnIsSyncedFlag
                    )
                )
            }
            finalDiff > 0 -> {
                //add expense
                transactionDao.save(
                    Transaction(
                        type = TransactionType.EXPENSE,
                        title = adjustTransactionTitle,
                        amount = diff.absoluteValue,
                        dateTime = timeNowUTC(),
                        accountId = account.id,
                        isSynced = trnIsSyncedFlag
                    )
                )
            }
        }
    }

    fun historyForAccount(account: Account, range: FromToTimeRange): List<TransactionHistoryItem> {
        val startDate = range.from()
        val endDate = range.to()

        return transactionDao
            .findAllByAccountAndBetween(
                accountId = account.id,
                startDate = startDate,
                endDate = endDate
            )
            .plus(
                transactionDao.findAllToAccountAndBetween(
                    toAccountId = account.id,
                    startDate = startDate,
                    endDate = endDate
                )
            )
            .sortedByDescending { it.dateTime }
            .withDateDividers(
                exchangeRatesLogic = exchangeRatesLogic,
                accountDao = accountDao,
                settingsDao = settingsDao
            )
    }

    fun calculateAccountBalance(
        account: Account,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Double {
        return calculateIncomeWithTransfers(
            account = account,
            startTime = startTime,
            endTime = endTime
        ) - calculateExpensesWithTransfers(
            account = account,
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun calculateIncomeWithTransfers(
        account: Account,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TransactionType.INCOME, account.id)
            .filterHappenedTransactions(
                startTime = startTime,
                endTime = endTime
            )
            .sumOf { it.amount }
            .plus(
                //transfers in
                transactionDao.findAllTransfersToAccount(account.id)
                    .filter { it.dateTime != null }
                    .sumOf { it.toAmount ?: it.amount }
            )
    }

    private fun calculateExpensesWithTransfers(
        account: Account,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TransactionType.EXPENSE, account.id)
            .filterHappenedTransactions(
                startTime = startTime,
                endTime = endTime
            )
            .sumOf { it.amount }
            .plus(
                //transfer out
                transactionDao.findAllByTypeAndAccount(
                    type = TransactionType.TRANSFER,
                    accountId = account.id
                )
                    .filter { it.dateTime != null }
                    .sumOf { it.amount }
            )
    }

    private fun List<Transaction>.filterHappenedTransactions(
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Transaction> {
        return this.filter {
            it.dateTime != null &&
                    (startTime == null || it.dateTime.isAfter(startTime)) &&
                    (endTime == null || it.dateTime.isBefore(endTime))
        }
    }

    fun calculateAccountIncome(account: Account, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.INCOME,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    fun calculateAccountExpenses(account: Account, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.EXPENSE,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    fun calculateUpcomingIncome(account: Account, range: FromToTimeRange): Double =
        upcoming(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    fun calculateUpcomingExpenses(account: Account, range: FromToTimeRange): Double =
        upcoming(account = account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    fun calculateOverdueIncome(account: Account, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    fun calculateOverdueExpenses(account: Account, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    fun upcoming(account: Account, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }


    fun overdue(account: Account, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }
}