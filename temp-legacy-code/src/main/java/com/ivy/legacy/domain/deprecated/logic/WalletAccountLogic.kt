package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.legacy.Transaction
import com.ivy.legacy.data.model.filterOverdue
import com.ivy.legacy.data.model.filterUpcoming
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.datamodel.toEntity
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.base.model.TransactionType
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.absoluteValue

@Deprecated("Migrate to FP Style")
class WalletAccountLogic @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionWriter: WriteTransactionDao,
) {

    suspend fun adjustBalance(
        account: Account,
        actualBalance: Double? = null,
        newBalance: Double,

        adjustTransactionTitle: String = "Adjust balance",

        isFiat: Boolean? = null,
        trnIsSyncedFlag: Boolean = false, // TODO: Remove this once Bank Integration trn sync is properly implemented
    ) {
        val ab = actualBalance ?: calculateAccountBalance(account)
        val diff = ab - newBalance

        val finalDiff = if (isFiat == true && abs(diff) < 0.009) 0.0 else diff
        when {
            finalDiff < 0 -> {
                // add income
                transactionWriter.save(
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
                // add expense
                transactionWriter.save(
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
                // transfers in
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
                // transfer out
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

    suspend fun calculateAccountIncome(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.INCOME,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateAccountExpenses(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        transactionDao
            .findAllByTypeAndAccountBetween(
                type = TransactionType.EXPENSE,
                accountId = account.id,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter { it.dateTime != null }
            .sumOf { it.amount }

    suspend fun calculateUpcomingIncome(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        upcoming(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateUpcomingExpenses(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        upcoming(account = account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueIncome(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.toDouble() }

    suspend fun calculateOverdueExpenses(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        overdue(account, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.toDouble() }

    suspend fun upcoming(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcoming()
    }

    suspend fun overdue(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionDao.findAllDueToBetweenByAccount(
            accountId = account.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdue()
    }
}
