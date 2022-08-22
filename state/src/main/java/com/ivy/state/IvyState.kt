package com.ivy.state

import com.ivy.data.ExchangeRates
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.category.Category

private var state = IvyState(
    accounts = null,
    categories = null,

    period = null,
    startDayOfMonth = null,

    baseCurrency = null,
    exchangeRates = null,
)

data class IvyState(
    val accounts: List<Account>?,
    val categories: List<Category>?,

    val period: Period?,
    val startDayOfMonth: Int?,

    val baseCurrency: String?,
    /**
     * The exchange rate baseCurrency <> CurrencyCode.
     */
    val exchangeRates: ExchangeRates?
)

fun readIvyState(): IvyState = state

fun writeIvyState(update: () -> IvyState) {
    state = update()
}

//region Update
fun baseCurrencyUpdate(newCurrency: String?): () -> IvyState = {
    readIvyState().copy(
        baseCurrency = newCurrency
    )
}

fun accountsUpdate(newAccounts: List<Account>?): () -> IvyState = {
    readIvyState().copy(
        accounts = newAccounts
    )
}

fun categoriesUpdate(newCategories: List<Category>?): () -> IvyState = {
    readIvyState().copy(
        categories = newCategories
    )
}

fun periodUpdate(newPeriod: Period?): () -> IvyState = {
    readIvyState().copy(
        period = newPeriod
    )
}

fun startDayOfMonth(newStartDay: Int?): () -> IvyState = {
    readIvyState().copy(
        startDayOfMonth = newStartDay
    )
}

fun exchangeRatesUpdate(newExchangeRates: ExchangeRates?): () -> IvyState = {
    readIvyState().copy(
        exchangeRates = newExchangeRates
    )
}

fun invalidate() = null
//endregion