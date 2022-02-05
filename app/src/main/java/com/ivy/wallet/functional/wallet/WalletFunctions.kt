package com.ivy.wallet.functional.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.functional.account.balanceValueFunction
import com.ivy.wallet.functional.account.expenseValueFunction
import com.ivy.wallet.functional.account.incomeValueFunction
import com.ivy.wallet.functional.core.Uncertain
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.data.CurrencyConvError
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal

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
            ::balanceValueFunction
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
    range: ClosedTimeRange = ClosedTimeRange.allTimeIvy(),
): Uncertain<List<CurrencyConvError>, IncomeExpense> {
    val uncertainValues = calculateWalletValues(
        accountDao = accountDao,
        transactionDao = transactionDao,
        exchangeRateDao = exchangeRateDao,
        baseCurrencyCode = baseCurrencyCode,
        filterExcluded = filterExcluded,
        range = range,
        valueFunctions = nonEmptyListOf(
            ::incomeValueFunction,
            ::expenseValueFunction
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