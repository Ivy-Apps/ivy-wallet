package com.ivy.search

import com.ivy.domain.datamodel.Account
import com.ivy.domain.datamodel.Category
import com.ivy.domain.datamodel.TransactionHistoryItem
import kotlinx.collections.immutable.ImmutableList

data class SearchState(
    val transactions: ImmutableList<TransactionHistoryItem>,
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)