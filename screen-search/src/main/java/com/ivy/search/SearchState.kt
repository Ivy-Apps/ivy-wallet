package com.ivy.search

import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import kotlinx.collections.immutable.ImmutableList

data class SearchState(
    val transactions: ImmutableList<TransactionHistoryItem>,
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)