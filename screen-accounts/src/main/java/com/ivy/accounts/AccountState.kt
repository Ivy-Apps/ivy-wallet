package com.ivy.accounts

import com.ivy.legacy.data.model.AccountData
import com.ivy.legacy.utils.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AccountState(
    val baseCurrency: String = "",
    val accountsData: ImmutableList<com.ivy.legacy.data.model.AccountData> = persistentListOf(),
    val totalBalanceWithExcluded: Double = 0.0,
    val totalBalanceWithExcludedText: com.ivy.legacy.utils.UiText = com.ivy.legacy.utils.UiText.DynamicString(""),
    val reorderVisible: Boolean = false
)
