package com.ivy.wallet.functional.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.functional.account.AccountValueFunctions
import com.ivy.wallet.functional.core.Uncertain
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.CurrencyConvError
import com.ivy.wallet.model.entity.Settings
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal

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
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, BigDecimal> {
    val uncertainValues = calculateWalletValues(
        accountDao = accountDao,
        transactionDao = transactionDao,
        exchangeRateDao = exchangeRateDao,
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

suspend fun calculateWalletIncomeExpense(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange,
): Uncertain<List<CurrencyConvError>, IncomeExpense> {
    val uncertainValues = calculateWalletValues(
        accountDao = accountDao,
        transactionDao = transactionDao,
        exchangeRateDao = exchangeRateDao,
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
        value = IncomeExpense(
            income = uncertainValues.value[0],
            expense = uncertainValues.value[1]
        )
    )
}

data class IncomeExpense(
    val income: BigDecimal,
    val expense: BigDecimal
)


suspend fun calculateWalletIncomeExpenseCount(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    filterExcluded: Boolean = true,
    range: ClosedTimeRange,
): Uncertain<List<CurrencyConvError>, Pair<BigDecimal, BigDecimal>> {
    val uncertainValues = calculateWalletValues(
        accountDao = accountDao,
        transactionDao = transactionDao,
        exchangeRateDao = exchangeRateDao,
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