package com.ivy.wallet.ui.accounts

import com.ivy.wallet.utils.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AccountState(
    val baseCurrency: String = "",
    val accountsData: ImmutableList<AccountData> = persistentListOf(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: UiText = UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)
