package com.ivy.wallet.domain.fp.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.domain.data.entity.Settings
import com.ivy.wallet.domain.fp.account.AccountValueFunctions
import com.ivy.wallet.domain.fp.core.Uncertain
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.domain.fp.data.CurrencyConvError
import com.ivy.wallet.domain.fp.data.IncomeExpensePair
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import java.math.BigDecimal
import java.util.*

fun walletBufferDiff(
    settings: Settings,
    balance: BigDecimal
): BigDecimal {
    return balance - settings.bufferAmount.toBigDecimal()
}

suspend fun baseCurrencyCode(
    settingsDao: SettingsDao
): String {
    return settingsDao.findFirst().currency
}

suspend fun calculateWalletBalance(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::balance
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletIncome(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::income
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletIncomeWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    accountIdFilterList: List<UUID>,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValuesWithAccountFilters(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        accountIdFilterList = accountIdFilterList,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::income
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletExpense(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::expense
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletExpenseWithAccountFilters(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    accountIdFilterList: List<UUID>,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValuesWithAccountFilters(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        accountIdFilterList = accountIdFilterList,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::expense
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = uncertainValues.value.head
    )
}

suspend fun calculateWalletIncomeExpense(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange,
): Uncertain<List<CurrencyConvError>, IncomeExpensePair> {
    val uncertainValues = calculateWalletValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::income,
            AccountValueFunctions::expense
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = IncomeExpensePair(
            income = uncertainValues.value[0],
            expense = uncertainValues.value[1]
        )
    )
}

suspend fun calculateWalletIncomeExpenseCount(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange,
): Uncertain<List<CurrencyConvError>, Pair<BigDecimal, BigDecimal>> {
    val uncertainValues = calculateWalletValues(
        walletDAOs = walletDAOs,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            AccountValueFunctions::incomeCount,
            AccountValueFunctions::expenseCount
        )
    )

    return Uncertain(
        error = uncertainValues.error,
        value = Pair(
            uncertainValues.value[0], uncertainValues.value[1]
        )
    )
}

//TODO: upcomingIncomeExpense
//TODO: overdueIncomeExpense
