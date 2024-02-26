package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.model.CategoryId
import com.ivy.data.repository.TransactionRepository
import com.ivy.legacy.data.model.filterOverdue
import com.ivy.legacy.data.model.filterOverdueLegacy
import com.ivy.legacy.data.model.filterUpcoming
import com.ivy.legacy.data.model.filterUpcomingLegacy
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.domain.pure.transaction.LegacyTrnDateDividers
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumInBaseCurrency
import java.util.UUID
import javax.inject.Inject

@Deprecated("Migrate to FP Style")
class WalletCategoryLogic @Inject constructor(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val transactionDao: TransactionDao,
    private val transactionRepository: TransactionRepository
) {

    suspend fun calculateCategoryBalance(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
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
                    TransactionType.INCOME -> amount
                    TransactionType.EXPENSE -> -amount
                    TransactionType.TRANSFER -> 0.0 // TODO: Transfer zero operation
                }
            }
    }

    suspend fun calculateCategoryIncome(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
                type = TransactionType.INCOME,
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
        incomeTransaction: List<Transaction>,
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
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
                type = TransactionType.EXPENSE,
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
        expenseTransactions: List<Transaction>,
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

    suspend fun calculateUnspecifiedBalance(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return calculateUnspecifiedIncome(range) - calculateUnspecifiedExpenses(range)
    }

    suspend fun calculateUnspecifiedIncome(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toDomain() }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUnspecifiedExpenses(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.EXPENSE,
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
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID>,
        transactions: List<Transaction> = emptyList()
    ): List<TransactionHistoryItem> {
        return with(LegacyTrnDateDividers) {
            historyByCategory(category, range, transactions = transactions)
                .filter {
                    accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
                }
                .withDateDividers(
                    exchangeRatesLogic = exchangeRatesLogic,
                    settingsDao = settingsDao,
                    accountDao = accountDao
                )
        }
    }

    suspend fun historyByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
    ): List<Transaction> {
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

    suspend fun historyUnspecified(range: com.ivy.legacy.data.model.FromToTimeRange): List<TransactionHistoryItem> {
        return with(LegacyTrnDateDividers) {
            transactionDao
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
    }

    suspend fun calculateUpcomingIncomeByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return upcomingByCategoryLegacy(category = category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return upcomingByCategoryLegacy(category = category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingIncomeUnspecified(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return upcomingUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesUnspecified(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return upcomingUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun upcomingByCategoryLegacy(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcomingLegacy()
    }

    suspend fun upcomingByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id),
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun upcomingUnspecifiedLegacy(range: com.ivy.legacy.data.model.FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(),
            endDate = range.to()
        )
            .map { it.toDomain() }
            .filterUpcomingLegacy()
    }

    suspend fun upcomingUnspecified(
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    suspend fun calculateOverdueIncomeByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return overdueByCategoryLegacy(category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return overdueByCategoryLegacy(category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueIncomeUnspecified(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return overdueUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesUnspecified(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        return overdueUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun overdueByCategoryLegacy(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdueLegacy()
    }

    suspend fun overdueByCategory(
        category: Category,
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id),
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .filterOverdue()
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun overdueUnspecifiedLegacy(range: com.ivy.legacy.data.model.FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo()
        )
            .map { it.toDomain() }
            .filterOverdueLegacy()
    }

    suspend fun overdueUnspecified(
        range: com.ivy.legacy.data.model.FromToTimeRange
    ): List<com.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }
}
