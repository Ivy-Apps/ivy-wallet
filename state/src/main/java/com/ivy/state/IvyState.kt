package com.ivy.state

import com.ivy.data.ExchangeRates
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.time.Period

@Deprecated("in favor of SharedFlowAction")
private var state = IvyState(
    accounts = null,
    categories = null,

    period = null,
    startDayOfMonth = null,

    baseCurrency = null,
    exchangeRates = null,
)

@Deprecated("in favor of SharedFlowAction")
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

@Deprecated("in favor of SharedFlowAction")
fun readIvyState(): IvyState = state

@Deprecated("in favor of SharedFlowAction")
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