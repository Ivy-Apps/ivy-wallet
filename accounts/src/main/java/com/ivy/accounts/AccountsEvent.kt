package com.ivy.accounts

import com.ivy.base.AccountData
import com.ivy.data.AccountOld

sealed class AccountsEvent {
    data class OnReorder(val reorderedList: List<AccountData>) : AccountsEvent()
    data class OnEditAccount(val editedAccount: AccountOld, val newBalance: Double) : AccountsEvent()
    data class OnReorderModalVisible(val reorderVisible: Boolean) : AccountsEvent()
}