package com.ivy.core.ui.account.pick

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.account.AccountUi

@Immutable
data class AccountPickerState(
    val accounts: List<AccountUi>,
)