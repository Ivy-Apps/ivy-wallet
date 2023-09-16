package com.ivy.accounts

import androidx.compose.runtime.Immutable
import com.ivy.legacy.data.model.AccountData
import com.ivy.legacy.utils.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AccountState(
    val baseCurrency: String = "",
    val accountsData: ImmutableList<AccountData> = persistentListOf(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: UiText = UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)
