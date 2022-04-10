package com.ivy.wallet.domain.logic

import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Category
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.logic.currency.sumInBaseCurrency
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.filterOverdue
import com.ivy.wallet.ui.onboarding.model.filterUpcoming
import java.util.*

@Deprecated("Migrate to FP Style")
class WalletCategoryLogic(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val transactionDao: TransactionDao
) {

    fun calculateCategoryBalance(
        category: Category,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
    ): Double {
        val baseCurrency = settingsDao.findFirst().currency
        val accounts = accountDao.findAll()

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
                    TransactionType.TRANSFER -> 0.0 //TODO: Transfer zero operation
                }
            }
    }

    fun calculateCategoryIncome(
        category: Category,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
                type = TransactionType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            )
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateCategoryIncome(
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

    fun calculateCategoryExpenses(
        category: Category,
        range: FromToTimeRange,
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
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    fun calculateCategoryExpenses(
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

    fun calculateUnspecifiedBalance(range: FromToTimeRange): Double {
        return calculateUnspecifiedIncome(range) - calculateUnspecifiedExpenses(range)
    }

    fun calculateUnspecifiedIncome(range: FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            )
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateUnspecifiedExpenses(range: FromToTimeRange): Double {
        return transactionDao
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.EXPENSE,
                startDate = range.from(),
                endDate = range.to()
            )
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun historyByCategoryWithDateDividers(
        category: Category,
        range: FromToTimeRange
    ): List<TransactionHistoryItem> {
        return historyByCategory(category, range)
            .withDateDividers(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun historyByCategoryAccountWithDateDividers(
        category: Category,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID>,
        transactions: List<Transaction> = emptyList()
    ): List<TransactionHistoryItem> {
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

    fun historyByCategory(
        category: Category,
        range: FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
    ): List<Transaction> {

        val trans = transactions.ifEmpty {
            transactionDao
                .findAllByCategoryAndBetween(
                    categoryId = category.id,
                    startDate = range.from(),
                    endDate = range.to()
                )
        }

        return trans.filter {
            accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
        }
    }

    fun historyUnspecified(range: FromToTimeRange): List<TransactionHistoryItem> {
        return transactionDao
            .findAllUnspecifiedAndBetween(
                startDate = range.from(),
                endDate = range.to()
            )
            .withDateDividers(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    fun calculateUpcomingIncomeByCategory(category: Category, range: FromToTimeRange): Double {
        return upcomingByCategory(category = category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateUpcomingExpensesByCategory(category: Category, range: FromToTimeRange): Double {
        return upcomingByCategory(category = category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateUpcomingIncomeUnspecified(range: FromToTimeRange): Double {
        return upcomingUnspecified(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateUpcomingExpensesUnspecified(range: FromToTimeRange): Double {
        return upcomingUnspecified(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun upcomingByCategory(category: Category, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    fun upcomingUnspecified(range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(),
            endDate = range.to()
        ).filterUpcoming()
    }

    fun calculateOverdueIncomeByCategory(category: Category, range: FromToTimeRange): Double {
        return overdueByCategory(category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateOverdueExpensesByCategory(category: Category, range: FromToTimeRange): Double {
        return overdueByCategory(category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateOverdueIncomeUnspecified(range: FromToTimeRange): Double {
        return overdueUnspecified(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    fun calculateOverdueExpensesUnspecified(range: FromToTimeRange): Double {
        return overdueUnspecified(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }


    fun overdueByCategory(category: Category, range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategory(
            categoryId = category.id,
            startDate = range.from(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }

    fun overdueUnspecified(range: FromToTimeRange): List<Transaction> {
        return transactionDao.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo()
        ).filterOverdue()
    }

}