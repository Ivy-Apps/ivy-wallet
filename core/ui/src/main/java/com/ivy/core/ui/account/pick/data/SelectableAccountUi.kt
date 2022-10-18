package com.ivy.core.ui.account.pick.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.account.AccountUi

@Immutable
data class SelectableAccountUi(
    val account: AccountUi,
    val selected: Boolean
)