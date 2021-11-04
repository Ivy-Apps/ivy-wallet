package com.ivy.wallet.logic

import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.currency.sumInBaseCurrency
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.TransactionDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.filterOverdue
import com.ivy.wallet.ui.onboarding.model.filterUpcoming

class WalletCategoryLogic(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val transactionDao: TransactionDao
) {

    fun calculateCategoryBalance(category: Category, range: FromToTimeRange): Double {
        val baseCurrency = settingsDao.findFirst().currency
        val accounts = accountDao.findAll()

        return historyByCategory(category, range = range)
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

    fun calculateCategoryIncome(category: Category, range: FromToTimeRange): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
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

    fun calculateCategoryExpenses(category: Category, range: FromToTimeRange): Double {
        return transactionDao
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id,
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

    fun historyByCategory(category: Category, range: FromToTimeRange): List<Transaction> {
        return transactionDao
            .findAllByCategoryAndBetween(
                categoryId = category.id,
                startDate = range.from(),
                endDate = range.to()
            )
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