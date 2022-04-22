package com.ivy.wallet.domain.pure.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.domain.data.entity.Settings
import com.ivy.wallet.domain.pure.account.AccountValueFunctions
import com.ivy.wallet.domain.pure.core.Uncertain
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.CurrencyConvError
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import java.math.BigDecimal
import java.util.*

fun walletBufferDiff(
    settings: Settings,
    balance: BigDecimal
): BigDecimal {
    return balance - settings.bufferAmount.toBigDecimal()
}

@Deprecated("Side-effects must be handled by Actions")
suspend fun baseCurrencyCode(
    settingsDao: SettingsDao
): String {
    return settingsDao.findFirst().currency
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
