package com.ivy.search

import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Category
import com.ivy.core.datamodel.TransactionHistoryItem
import kotlinx.collections.immutable.ImmutableList

data class SearchState(
    val transactions: ImmutableList<TransactionHistoryItem>,
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)