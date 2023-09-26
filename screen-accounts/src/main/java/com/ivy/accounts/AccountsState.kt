package com.ivy.accounts

import com.ivy.legacy.data.model.AccountData
import com.ivy.legacy.utils.UiText
import kotlinx.collections.immutable.ImmutableList

data class AccountsState(
    val baseCurrency: String,
    val accountsData: ImmutableList<AccountData>,
    val totalBalanceWithExcluded: String,
    val totalBalanceWithExcludedText: UiText,
    val reorderVisible: Boolean
)