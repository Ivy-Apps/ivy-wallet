package com.ivy.accounts

import com.ivy.legacy.data.model.AccountData
import kotlinx.collections.immutable.ImmutableList

data class AccountState(
    val baseCurrency: String,
    val accountsData: ImmutableList<AccountData>,
    val totalBalanceWithExcluded: String,
    val totalBalanceWithExcludedText: String,
    val reorderVisible: Boolean
)