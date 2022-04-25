package com.ivy.wallet.ui.accounts

import com.ivy.wallet.utils.UiText

data class AccountState(
    val baseCurrency: String = "",
    val accountsData: List<AccountData> = emptyList(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: UiText = UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)