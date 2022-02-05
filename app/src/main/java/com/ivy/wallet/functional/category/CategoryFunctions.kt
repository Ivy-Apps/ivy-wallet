package com.ivy.wallet.functional.category

import arrow.core.nonEmptyListOf
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.util.*

suspend fun calculateCategoryBalance(
    transactionDao: TransactionDao,
    accountDao: AccountDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValues(
        transactionDao = transactionDao,
        accountDao = accountDao,
        exchangeRateDao = exchangeRateDao,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::balance
        )
    ).head
}

data class CategoryStats(
    val balance: BigDecimal,
    val income: BigDecimal,
    val expense: BigDecimal,
    val incomeCount: BigDecimal,
    val expenseCount: BigDecimal
)

suspend fun calculateCategoryStats(
    transactionDao: TransactionDao,
    accountDao: AccountDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): CategoryStats {
    val values = calculateCategoryValues(
        transactionDao = transactionDao,
        accountDao = accountDao,
        exchangeRateDao = exchangeRateDao,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::income,
            CategoryValueFunctions::expense,
            CategoryValueFunctions::incomeCount,
            CategoryValueFunctions::expenseCount
        )
    )

    val income = values[0]
    val expense = values[1]

    return CategoryStats(
        balance = income - expense,
        income = income,
        expense = expense,
        incomeCount = values[2],
        expenseCount = values[3]
    )
}