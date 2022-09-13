package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.FromToTimeRange
import com.ivy.base.filterOverdue
import com.ivy.base.filterUpcoming
import com.ivy.common.timeNowUTC
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
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
        account: AccountOld,
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
                    TransactionOld(
                        type = TrnTypeOld.INCOME,
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
                    TransactionOld(
                        type = TrnTypeOld.EXPENSE,
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
        account: AccountOld,
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
        account: AccountOld,
        before: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TrnTypeOld.INCOME, account.id)
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
        account: AccountOld,
        before: LocalDateTime?
    ): Double {
        return transactionDao.findAllByTypeAndAccount(TrnTypeOld.EXPENSE, account.id)
            .map { it.toDomain() }
            .filterHappenedTransactions(
                before = before
            )
            .sumOf { it.amount.toDouble() }
            .plus(
                //transfer out
                transactionDao.findAllByTypeAndAccount(
                    type = TrnTypeOld.TRANSFER,
                    accountId = account.id
                )
                    .map { it.toDomain() }
                    .filterHappenedTransactions(
                        before = before
                    )
                    .sumOf { it.amount.toDouble() }
            )
    }

    private fun List<TransactionOld>.filterHappenedTransactions(
        before: LocalDateTime?
    ): List<TransactionOld> {
        return this.filter {
            it.dateTime != null &&
                    (before == null || it.dateTime!!.isBefore(before))
        }
    }

    suspend fun calculateAccountIncome(account: AccountOld, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TrnTypeOld.INCOME,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateAccountExpenses(account: AccountOld, range: FromToTimeRange): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TrnTypeOld.EXPENSE,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateUpcomingIncome(account: AccountOld, range: FromToTimeRange): Double =
        upcoming(account, range = range)
            .filter { it.type == TrnTypeOld.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateUpcomingExpenses(account: AccountOld, range: FromToTimeRange): Double =
        upcoming(account = account, range = range)
            .filter { it.type == TrnTypeOld.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueIncome(account: AccountOld, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TrnTypeOld.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueExpenses(account: AccountOld, range: FromToTimeRange): Double =
        overdue(account, range = range)
            .filter { it.type == TrnTypeOld.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun upcoming(account: AccountOld, range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcoming()
    }


    suspend fun overdue(account: AccountOld, range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdue()
    }
}