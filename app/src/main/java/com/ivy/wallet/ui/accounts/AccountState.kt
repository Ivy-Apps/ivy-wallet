package com.ivy.wallet.ui.accounts

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.wallet.utils.UiText
import com.ivy.wallet.utils.emptyImmutableList

data class AccountState(
    val baseCurrency: String = "",
    val accountsData: ImmutableList<AccountData> = emptyImmutableList(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: UiText = UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)
