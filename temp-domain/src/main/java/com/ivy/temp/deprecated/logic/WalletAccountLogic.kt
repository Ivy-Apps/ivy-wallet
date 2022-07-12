package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.FromToTimeRange
import com.ivy.base.filterOverdue
import com.ivy.base.filterUpcoming
import com.ivy.common.timeNowUTC
import com.ivy.data.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.io.persistence.data.toEntity
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.absoluteValue

@Deprecated("Migrate to FP Style")
class WalletAccountLogic(
    private val transactionDao: TransactionDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao
) {

    suspend fun adjustBalance(
        account: Account,
        actualBalance: Double? = null,
        newBalance: Double,

        adjustTransactionTitle: String = "Adjust balance",

        isFiat: Boolean? = null,
        trnIsSyncedFlag: Boolean = false, //TODO: Remove this once Bank Integration trn sync is properly implemented
    ) {
        val ab = actualBalance ?: calculateAccountBalance(account)
        val diff = ab - newBalance

        val finalDiff = if (isFiat == true && abs(diff) < 0.009) 0.0 else diff
        when {
            finalDiff < 0 -> {
                //add income
                transactionDao.save(
                    Transaction(
                        type = TransactionType.INCOME,
                        title = adjustTransactionTitle,
                        amount = diff.absoluteValue.toBigDecimal(),
                        toAmount = diff.absoluteValue.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        accountId = account.id,
                        isSynced = trnIsSyncedFlag
                    ).toEntity()
                )
            }
            finalDiff > 0 -> {
                //add expense
                transactionDao.save(
                    Transaction(
                        type = TransactionType.EXPENSE,
                        title = adjustTransactionTitle,
                        amount = diff.absoluteValue.toBigDecimal(),
                        toAmount = diff.absoluteValue.toBigDecimal(),
                        dateTime = timeNowUTC(),
                        accountId = account.id,
                        isSynced = trnIsSyncedFlag
                    ).toEntity()
                )
            }
        }
    }

    suspend fun calculateAccountBalance(
        account: Account,
        before: LocalDateTime? = null
    ): Double {
        return calculateIncomeWithTransfers(
            account = account,
            before = before
        ) - calculateExpensesWithTransfers(
            account = account,
            before = before
        )
    }

    private suspend fun calculateIncomeWithTransfers(
        account: Account,
        before: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TransactionType.INCOME, account.id)
            .map { it.toDomain() }
            .filterHappenedTransactions(
                before = before
            )
            .sumOf { it.amount.toDouble() }
            .plus(
                //transfers in
                transactionDao.findAllTransfersToAccount(account.id)
                    .map { it.toDomain() }
                    .filterHappenedTransactions(
                        before = before
                    )
                    .sumOf { it.toAmount.toDouble() }
            )
    }

    private suspend fun calculateExpensesWithTransfers(
        account: Account,
        before: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TransactionType.EXPENSE, account.id)
            .map { it.toDomain() }
            .filterHappenedTransactions(
                before = before
            )
            .sumOf { it.amount.toDouble() }
            .plus(
                //transfer out
                transactionDao.findAllByTypeAndAccount(
                    type = TransactionType.TRANSFER,
                    accountId = account.id
                )
                    .map { it.toDomain() }
                    .filterHappenedTransactions(
                        before = before
                    )
                    .sumOf { it.amount.toDouble() }
            )
    }

    private fun List<Transaction>.filterHappenedTransactions(
        before: LocalDateTime?
    ): List<Transaction> {
        return this.filter {
            it.dateTime != null &&
                    (before == null || it.dateTime!!.isBefore(before))
        }
    }

    suspend fun calculateAccountIncome(account: Account, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.INCOME,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateAccountExpenses(account: Account, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.EXPENSE,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateUpcomingIncome(account: Account, range: FromToTimeRange): Double =
        upcoming(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateUpcomingExpenses(account: Account, range: FromToTimeRange): Double =
        upcoming(account = account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueIncome(account: Account, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueExpenses(account: Account, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun upcoming(account: Account, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcoming()
    }


    suspend fun overdue(account: Account, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdue()
    }
}