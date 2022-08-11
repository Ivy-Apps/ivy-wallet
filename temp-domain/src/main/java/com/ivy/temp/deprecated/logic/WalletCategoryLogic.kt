package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.FromToTimeRange
import com.ivy.base.filterOverdue
import com.ivy.base.filterUpcoming
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumInBaseCurrency
import com.ivy.wallet.domain.pure.transaction.withDateDividers
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*

@Deprecated("Migrate to FP Style")
class WalletCategoryLogic(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val transactionDao: TransactionDao
) {

    suspend fun calculateCategoryBalance(
        category: CategoryOld,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<TransactionOld> = emptyList()
    ): Double {
        val baseCurrency = settingsDao.findFirst().currency
        val accounts = accountDao.findAll().map { it.toDomain() }

        return historyByCategory(
            category,
            range = range,
            accountFilterSet = accountFilterSet,
            transactions = transactions
        )
            .sumOf {
                val amount = exchangeRatesLogic.amountBaseCurrency(
                    transaction = it,
                    baseCurrency = baseCurrency,
                    accounts = accounts
                )

                when (it.type) {
                    TrnType.INCOME -> amount
                    TrnType.EXPENSE -> -amount
                    TrnType.TRANSFER -> 0.0 //TODO: Transfer zero operation
                }
            }
    }

    suspend fun calculateCategoryIncome(
        category: CategoryOld,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
                type = TrnType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toDomain() }
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateCategoryIncome(
        incomeTransaction: List<TransactionOld>,
        accountFilterSet: Set<UUID> = emptySet()
    ): Double {
        return incomeTransaction
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateCategoryExpenses(
        category: CategoryOld,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
                type = TrnType.EXPENSE,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }.map { it.toDomain() }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    suspend fun calculateCategoryExpenses(
        expenseTransactions: List<TransactionOld>,
        accountFilterSet: Set<UUID> = emptySet()
    ): Double {
        return expenseTransactions
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUnspecifiedBalance(range: FromToTimeRange): Double {
        return calculateUnspecifiedIncome(range) - calculateUnspecifiedExpenses(range)
    }

    suspend fun calculateUnspecifiedIncome(range: FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TrnType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toDomain() }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUnspecifiedExpenses(range: FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TrnType.EXPENSE,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toDomain() }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    suspend fun historyByCategoryAccountWithDateDividers(
        category: CategoryOld,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID>,
        transactions: List<TransactionOld> = emptyList()
    ): List<Any> {
        return historyByCategory(category, range, transactions = transactions)
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .withDateDividers(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun historyByCategory(
        category: CategoryOld,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<TransactionOld> = emptyList()
    ): List<TransactionOld> {

        val trans = transactions.ifEmpty {
            transactionDao
                .findAllByCategoryAndBetween(
                    categoryId = category.id,
                    startDate = range.from(),
                    endDate = range.to()
                ).map { it.toDomain() }
        }

        return trans.filter {
            accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
        }
    }

    suspend fun historyUnspecified(range: FromToTimeRange): List<Any> {
        return transactionDao
            .findAllUnspecifiedAndBetween(
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toDomain() }
            .withDateDividers(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    suspend fun calculateUpcomingIncomeByCategory(
        category: CategoryOld,
        range: FromToTimeRange
    ): Double {
        return upcomingByCategory(category = category, range = range)
            .filter { it.type == TrnType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesByCategory(
        category: CategoryOld,
        range: FromToTimeRange
    ): Double {
        return upcomingByCategory(category = category, range = range)
            .filter { it.type == TrnType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingIncomeUnspecified(range: FromToTimeRange): Double {
        return upcomingUnspecified(range = range)
            .filter { it.type == TrnType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesUnspecified(range: FromToTimeRange): Double {
        return upcomingUnspecified(range = range)
            .filter { it.type == TrnType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun upcomingByCategory(category: CategoryOld, range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcoming()
    }

    suspend fun upcomingUnspecified(range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcoming()
    }

    suspend fun calculateOverdueIncomeByCategory(
        category: CategoryOld,
        range: FromToTimeRange
    ): Double {
        return overdueByCategory(category, range = range)
            .filter { it.type == TrnType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesByCategory(
        category: CategoryOld,
        range: FromToTimeRange
    ): Double {
        return overdueByCategory(category, range = range)
            .filter { it.type == TrnType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueIncomeUnspecified(range: FromToTimeRange): Double {
        return overdueUnspecified(range = range)
            .filter { it.type == TrnType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesUnspecified(range: FromToTimeRange): Double {
        return overdueUnspecified(range = range)
            .filter { it.type == TrnType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    suspend fun overdueByCategory(category: CategoryOld, range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdue()
    }

    suspend fun overdueUnspecified(range: FromToTimeRange): List<TransactionOld> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdue()
    }

}