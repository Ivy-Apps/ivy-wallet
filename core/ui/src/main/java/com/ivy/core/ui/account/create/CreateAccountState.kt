package com.ivy.core.ui.account.create

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode

@Immutable
internal data class CreateAccountState(
    val currency: CurrencyCode,
    val icon: ItemIcon
)