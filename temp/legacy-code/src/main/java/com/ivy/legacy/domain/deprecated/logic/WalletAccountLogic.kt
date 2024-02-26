package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.getValue
import com.ivy.data.repository.TransactionRepository
import com.ivy.legacy.data.model.filterOverdue
import com.ivy.legacy.data.model.filterUpcoming
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.toEntity
import com.ivy.legacy.utils.timeNowUTC
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.absoluteValue

@Deprecated("Migrate to FP Style")
class WalletAccountLogic @Inject constructor(
    private val transactionRepository: TransactionRepository,
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
        return transactionRepository.findAllIncomeByAccount(AccountId(account.id))
            .filterHappenedTransactions(
                before = before
            )
            .sumOf { it.getValue().toDouble() }
            .plus(
                // transfers in
                transactionRepository.findAllTransfersToAccount(AccountId(account.id))
                    .filterHappenedTransactions(
                        before = before
                    )
                    .sumOf { it.getValue().toDouble() }
            )
    }

    private suspend fun calculateExpensesWithTransfers(
        account: Account,
        before: LocalDateTime?
    ): Double {
        return transactionRepository.findAllExpenseByAccount(AccountId(account.id))
            .filterHappenedTransactions(
                before = before
            )
            .sumOf { it.getValue().toDouble() }
            .plus(
                // transfer out
                transactionRepository.findAllTransferByAccount(
                    accountId = AccountId(account.id)
                )
                    .filterHappenedTransactions(before = before)
                    .sumOf { it.getValue().toDouble() }
            )
    }

    private fun List<com.ivy.data.model.Transaction>.filterHappenedTransactions(
        before: LocalDateTime?
    ): List<com.ivy.data.model.Transaction> {
        return this.filter {
            it.settled &&
                    (before == null || it.time.isBefore(before.toInstant(ZoneOffset.UTC)))
        }
    }

    suspend fun calculateUpcomingIncome(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        upcoming(account, range = range)
            .filterIsInstance<Income>()
            .sumOf { it.getValue().toDouble() }

    suspend fun calculateUpcomingExpenses(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        upcoming(account = account, range = range)
            .filterIsInstance<Expense>()
            .sumOf { it.getValue().toDouble() }

    suspend fun calculateOverdueIncome(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        overdue(account, range = range)
            .filterIsInstance<Income>()
            .sumOf { it.getValue().toDouble() }

    suspend fun calculateOverdueExpenses(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double =
        overdue(account, range = range)
            .filterIsInstance<Expense>()
            .sumOf { it.getValue().toDouble() }

    suspend fun upcoming(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByAccount(
            accountId = AccountId(account.id),
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    suspend fun overdue(
        account: Account,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByAccount(
            accountId = AccountId(account.id),
            startDate = range.from(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }
}
