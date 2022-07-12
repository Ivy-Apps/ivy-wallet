package com.ivy.accounts

import com.ivy.base.AccountData
import com.ivy.base.UiText

data class AccountState(
    val baseCurrency: String = "",
    val accountsData: List<AccountData> = emptyList(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: UiText = UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)