package com.ivy.core.ui.account.pick

import com.ivy.core.ui.data.account.AccountUi

sealed interface AccountPickerEvent {
    data class SelectedChange(val selected: List<AccountUi>) : AccountPickerEvent
}