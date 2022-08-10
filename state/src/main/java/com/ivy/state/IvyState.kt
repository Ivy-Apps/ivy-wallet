package com.ivy.state

import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.category.Category

private var state = IvyState(
    accounts = null,
    categories = null,
    period = null,
    baseCurrency = null
)

data class IvyState(
    val accounts: List<Account>?,
    val categories: List<Category>?,
    val period: Period?,
    val baseCurrency: String?
)

fun readIvyState(): IvyState = state

fun writeIvyState(update: () -> IvyState) {
    state = update()
}

//region Update
fun baseCurrencyUpdate(newCurrency: String): () -> IvyState = {
    readIvyState().copy(
        baseCurrency = newCurrency
    )
}

fun accountsUpdate(newAccounts: List<Account>): () -> IvyState = {
    readIvyState().copy(
        accounts = newAccounts
    )
}

fun categoriesUpdate(newCategories: List<Category>): () -> IvyState = {
    readIvyState().copy(
        categories = newCategories
    )
}

fun periodUpdate(newPeriod: Period): () -> IvyState = {
    readIvyState().copy(
        period = newPeriod
    )
}
//endregion