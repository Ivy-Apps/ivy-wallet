package com.ivy.wallet.functional.category

import arrow.core.nonEmptyListOf
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.WalletDAOs
import java.math.BigDecimal
import java.util.*

suspend fun calculateCategoryBalance(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValues(
        walletDAOs = walletDAOs,
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
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): CategoryStats {
    val values = calculateCategoryValues(
        walletDAOs = walletDAOs,
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

suspend fun calculateCategoryIncome(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::income
        )
    ).head
}

suspend fun calculateCategoryIncomeWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    accountIdFilterList: List<UUID>,
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValuesWithAccountFilters(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        accountIdFilterSet = accountIdFilterList.toHashSet(),
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::income
        )
    ).head
}

suspend fun calculateCategoryExpense(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::expense
        )
    ).head
}

suspend fun calculateCategoryExpenseWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    categoryId: UUID?,
    accountIdList: List<UUID> = emptyList(),
    range: ClosedTimeRange,
): BigDecimal {
    return calculateCategoryValuesWithAccountFilters(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        categoryId = categoryId,
        range = range,
        accountIdFilterSet = accountIdList.toHashSet(),
        valueFunctions = nonEmptyListOf(
            CategoryValueFunctions::expense
        )
    ).head
}