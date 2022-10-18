package com.ivy.core.ui.account.pick

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.account.pick.data.SelectableAccountUi

@Immutable
data class AccountPickerState(
    val accounts: List<SelectableAccountUi>,
)